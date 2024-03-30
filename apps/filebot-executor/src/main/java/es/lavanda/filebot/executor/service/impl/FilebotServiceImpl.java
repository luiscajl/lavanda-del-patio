package es.lavanda.filebot.executor.service.impl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.lavanda.filebot.executor.amqp.ProducerService;
import es.lavanda.filebot.executor.exception.FilebotAMCException;
import es.lavanda.filebot.executor.model.FilebotCommandExecution;
import es.lavanda.filebot.executor.model.FilebotExecution;
import es.lavanda.filebot.executor.model.FilebotExecution.FileExecutor;
import es.lavanda.filebot.executor.model.FilebotExecution.FilebotStatus;
import es.lavanda.filebot.executor.repository.FilebotExecutionRepository;
import es.lavanda.filebot.executor.service.FilebotAMCExecutor;
import es.lavanda.filebot.executor.service.FilebotExecutionService;
import es.lavanda.filebot.executor.service.FilebotService;
import es.lavanda.filebot.executor.util.FilebotUtils;
import es.lavanda.lib.common.model.FilebotExecutionIDTO;
import es.lavanda.lib.common.model.FilebotExecutionTestIDTO;
import es.lavanda.lib.common.model.filebot.FilebotAction;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FilebotServiceImpl implements FilebotService {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private FilebotAMCExecutor filebotAMCExecutor;

    @Autowired
    private FilebotUtils filebotUtils;

    @Autowired
    private FilebotExecutionRepository filebotExecutionRepository;// FIXME: DEPENDENCIA CIRCULAR

    private static final Pattern PATTERN_SELECT_CONTENT = Pattern.compile("Group:.*=> \\[(.*)\\]");

    private static final Pattern PATTERN_MOVED_CONTENT = Pattern.compile("\\[(.*)\\] from \\[(.*)\\] to \\[(.*)\\]");

    private static final Pattern PATTERN_FILE_EXIST = Pattern
            .compile("Skipped \\[(.*)\\] because \\[(.*)\\] already exists");

    @Override
    public void execute(FilebotExecution filebotExecution) {
        log.info("On Execution");
        Runnable runnableTask = () -> {
            try {
                executionWithCommand(filebotExecution);
            } catch (Exception e) {
                log.error("Error executing filebot", e);
                throw new RuntimeException("Error executing filebot", e);
            }
        };
        executorService.execute(runnableTask);

    }

    private void executionWithCommand(FilebotExecution filebotExecution) {
        log.info("On Execution With Command: {}", filebotExecution);
        FilebotCommandExecution execution = new FilebotCommandExecution();
        try {
            filebotExecution.setStatus(FilebotStatus.ON_FILEBOT_EXECUTION);
            filebotExecution.setLog("");
            filebotExecutionRepository.save(filebotExecution);
            execution = filebotAMCExecutor
                    .execute(filebotExecution.getCommand()); // FIXME:
            log.info("Result of Execution {}", execution);
            filebotExecution.setLog(execution.getLog());
            filebotExecutionRepository.save(filebotExecution);
            completedFilebotExecution(filebotExecution, execution);
        } catch (FilebotAMCException e) {
            execution = e.getFilebotCommandExecution();
            filebotExecution.setLog(execution.getLog());
            filebotExecutionRepository.save(filebotExecution);
            handleException(filebotExecution, e.getFilebotCommandExecution(), e);
        }
    }

    private void handleException(FilebotExecution filebotExecution, FilebotCommandExecution execution,
            FilebotAMCException e) {
        switch (e.getType()) {
            case STRICT_QUERY:
                log.info("Handling STRICT_QUERY");
                strictOrQuery(filebotExecution, execution);
                break;
            case REGISTER:
                log.info("Handling REGISTER");
                filebotExecution.setStatus(FilebotStatus.ERROR);
                filebotExecutionRepository.save(filebotExecution);
                tryLicensed();// Send notification;
                break;
            case SELECTED_OPTIONS:
                log.info("Handling SELECTED_OPTIONS");
                // selectOptions(filebotExecution, execution);
                break;
            case FILE_EXIST:
                log.info("File exist");
                fileExist(filebotExecution, execution);
                break;
            case FILES_NOT_FOUND:
                log.info("Files not found");
                filebotExecution.setStatus(FilebotStatus.FILES_NOT_FOUND);
                filebotExecutionRepository.save(filebotExecution);
                //FIXME: SENDMESSAGE TO TELEGRAM TO INFORM
                break;
            case ERROR:
                log.info("Error on execution.");
                filebotExecution.setStatus(FilebotStatus.ERROR);
                filebotExecutionRepository.save(filebotExecution);
                break;
            default:
                break;
        }
    }

    private void fileExist(FilebotExecution filebotExecution, FilebotCommandExecution execution) {
        log.info("FileExist {}", execution);
        Matcher matcherMovedContent = PATTERN_FILE_EXIST.matcher(execution.getLog());
        List<FileExecutor> filesExecutor = new ArrayList<>();
        while (matcherMovedContent.find()) {
            String fromContent = matcherMovedContent.group(1);
            String toContent = matcherMovedContent.group(2);
            log.info("From Content {}", fromContent);
            log.info("To Content {}", toContent);
            FileExecutor fileExecutor = new FileExecutor();
            fileExecutor.setFile(fromContent);
            fileExecutor.setNewFile(toContent);
            filesExecutor.add(fileExecutor);
        }
        // filebotExecution.setNewParentPath(getFolderPathOfFiles(newFilesname));
        filebotExecution.setFiles(filesExecutor);
        filebotExecution.setStatus(FilebotStatus.FILES_EXISTED_IN_DESTINATION);
        filebotExecution.setLog(execution.getLog());
        filebotExecutionRepository.save(filebotExecution);
    }

    private void tryLicensed() {
        log.info("Try to register license");
        try {
            // notificationService.sendNotification(SnsTopic.FILEBOT_LICENSE_ERROR,
            // "Filebot License Error",
            // "Filebot License Error");
            filebotAMCExecutor.execute(filebotUtils.getRegisterCommand());
            log.info("Licensed");
        } catch (FilebotAMCException e) {
            log.info("Not Licensed");
            throw e;
        }
    }

    private void strictOrQuery(FilebotExecution filebotExecution, FilebotCommandExecution execution) {
        Matcher matcherGroupContent = PATTERN_SELECT_CONTENT.matcher(execution.getLog());
        List<String> groupContentList = new ArrayList<>();
        while (matcherGroupContent.find()) {
            String groupContent = matcherGroupContent.group(1);
            String[] groupContentSplit = groupContent.split(",");
            for (String splited : groupContentSplit) {
                groupContentList.add(splited);
            }
        }
        List<FileExecutor> filesExecutor = new ArrayList<>();
        for (String content : groupContentList) {
            FileExecutor fileExecutor = new FileExecutor();
            fileExecutor.setFile(content);
            filesExecutor.add(fileExecutor);
        }
        filebotExecution.setFiles(filesExecutor);
        FilebotExecutionIDTO filebotExecutionIDTO = new FilebotExecutionIDTO();
        filebotExecutionIDTO.setId(filebotExecution.getId());
        filebotExecutionIDTO.setFiles(filesExecutor.stream().map(fe -> fe.getFile()).collect(Collectors.toList()));
        filebotExecutionIDTO.setPath(filebotExecution.getPath().toString());
        filebotExecutionIDTO.setName(filebotExecution.getName());
        producerService.sendFilebotExecutionToTelegram(filebotExecutionIDTO);
        filebotExecution.setStatus(FilebotStatus.ON_TELEGRAM);
        filebotExecution.setLog(execution.getLog());
        filebotExecutionRepository.save(filebotExecution);
    }

    private void completedFilebotExecution(FilebotExecution filebotExecution, FilebotCommandExecution execution) {
        log.info("CompletedFilebotExecution {}", execution);

        if (filebotExecution.isOnTestPhase()) {
            completedExecutionTest(filebotExecution, execution);

        } else {
            completedExecution(filebotExecution, execution);
        }
    }

    private void completedExecutionTest(FilebotExecution filebotExecution, FilebotCommandExecution execution) {
        Matcher matcherMovedContent = PATTERN_MOVED_CONTENT.matcher(execution.getLog());
        List<String> oldFilesName = new ArrayList<>();
        List<String> newFilesname = new ArrayList<>();
        while (matcherMovedContent.find()) {
            String fromContent = matcherMovedContent.group(2);
            String toContent = matcherMovedContent.group(3);
            log.info("From Content {}", fromContent);
            log.info("To Content {}", toContent);
            oldFilesName.add(Path.of(fromContent).getFileName().toString());
            newFilesname.add(Path.of(toContent).getFileName().toString());
        }
        List<FileExecutor> filesExecutor = new ArrayList<>();
        for (int i = 0; i < oldFilesName.size(); i++) {
            FileExecutor fileExecutor = new FileExecutor();
            fileExecutor.setFile(oldFilesName.get(i));
            fileExecutor.setNewFile(newFilesname.get(i));
            filesExecutor.add(fileExecutor);
        }
        FilebotExecutionTestIDTO filebotExecutionTestIDTO = new FilebotExecutionTestIDTO();
        filebotExecutionTestIDTO.setId(filebotExecution.getId());
        filebotExecutionTestIDTO.setFiles(oldFilesName.stream().map(fe -> fe).collect(Collectors.toList()));
        filebotExecutionTestIDTO.setPossibilities(newFilesname.stream().map(fe -> fe).collect(Collectors.toList()));
        filebotExecutionTestIDTO.setPath(filebotExecution.getPath().toString());
        filebotExecutionTestIDTO.setName(filebotExecution.getName());
        filebotExecution.setLog(execution.getLog());
        filebotExecution.setStatus(FilebotStatus.ON_TELEGRAM_TEST);
        filebotExecutionRepository.save(filebotExecution);
        producerService.sendFilebotToConfirmTest(filebotExecutionTestIDTO);
    }

    private void completedExecution(FilebotExecution filebotExecution, FilebotCommandExecution execution) {
        Matcher matcherMovedContent = PATTERN_MOVED_CONTENT.matcher(execution.getLog());
        List<String> oldFilesName = new ArrayList<>();
        List<String> newFilesname = new ArrayList<>();
        String newParentFolderPath = null;
        while (matcherMovedContent.find()) {
            String fromContent = matcherMovedContent.group(2);
            String toContent = matcherMovedContent.group(3);
            log.info("From Content {}", fromContent);
            log.info("To Content {}", toContent);
            oldFilesName.add(Path.of(fromContent).getFileName().toString());
            newFilesname.add(Path.of(toContent).getFileName().toString());
            newParentFolderPath = Path.of(toContent).getParent().getFileName().toString();
        }
        List<FileExecutor> filesExecutor = new ArrayList<>();
        for (int i = 0; i < oldFilesName.size(); i++) {
            FileExecutor fileExecutor = new FileExecutor();
            fileExecutor.setFile(oldFilesName.get(i));
            fileExecutor.setNewFile(newFilesname.get(i));
            filesExecutor.add(fileExecutor);
        }
        filebotExecution.setFiles(filesExecutor);
        filebotExecution.setNewPath(newParentFolderPath);
        filebotExecution.setStatus(FilebotStatus.PROCESSED);
        if (FilebotAction.MOVE.equals(filebotExecution.getAction())) {
            // TODO: SEND MESSAGE TO TELEGRAM TO CONFIRM DELETE WITH LS TO SEE THE FILES TO
            // DELETE
            // fileService.rmdir(filebotExecution.getPath().toString());
        }
        filebotExecutionRepository.save(filebotExecution);
    }

}
