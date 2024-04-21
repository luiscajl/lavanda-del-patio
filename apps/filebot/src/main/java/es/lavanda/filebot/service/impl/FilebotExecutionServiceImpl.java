package es.lavanda.filebot.service.impl;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import es.lavanda.filebot.model.FilebotExecution;
import es.lavanda.filebot.model.QbittorrentModel;
import es.lavanda.filebot.repository.FilebotExecutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import es.lavanda.filebot.amqp.ProducerService;
import es.lavanda.filebot.service.FileService;
import es.lavanda.filebot.service.FilebotExecutionService;
import es.lavanda.filebot.service.FilebotService;
import es.lavanda.filebot.util.FilebotUtils;
import es.lavanda.lib.common.model.FilebotExecutionIDTO;
import es.lavanda.lib.common.model.FilebotExecutionODTO;
import es.lavanda.lib.common.model.FilebotExecutionTestODTO;
import es.lavanda.lib.common.model.filebot.FilebotCategory;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FilebotExecutionServiceImpl implements FilebotExecutionService {

    @Autowired
    private FilebotExecutionRepository filebotExecutionRepository;

    @Autowired
    private FilebotUtils filebotUtils;

    @Autowired
    private ProducerService producerService;

    @Autowired
    private FileService fileService;

    @Autowired
    private FilebotService filebotService;

    @Value("${lavanda.namespace}")
    private String KUBERNETES_NAMESPACE;

    @Override
    public Page<FilebotExecution> getAllPageable(Pageable pageable, String status, String path) {
        if (Objects.nonNull(status) && Objects.nonNull(path)) {
            // log.info("findAllByStatusAndPath");
            return filebotExecutionRepository.findAllByStatusAndPathContainingIgnoreCaseOrderByLastModifiedAtDesc(pageable,
                    status, path);
        } else if (Objects.nonNull(path)) {
            // log.info("findAllByPath");
            return filebotExecutionRepository.findAllByPathIgnoreCaseContainingOrderByLastModifiedAtDesc(pageable, path);
        } else if (Objects.nonNull(status)) {
            // log.info("findAllByStatus");
            return filebotExecutionRepository.findAllByStatusOrderByLastModifiedAtDesc(pageable, status);
        }
        log.debug("findAllByOrderByLastModifiedAtDesc");

        return filebotExecutionRepository.findAllByOrderByLastModifiedAtDesc(pageable);
    }

    @Override
    public void resolutionTelegramBot(FilebotExecutionODTO filebotExecutionODTO) {
        Optional<FilebotExecution> optFilebotExecution = filebotExecutionRepository
                .findById(filebotExecutionODTO.getId());
        if (optFilebotExecution.isPresent()
                && Boolean.FALSE.equals(optFilebotExecution.get().getStatus() == FilebotExecution.FilebotStatus.PROCESSED)) {
            FilebotExecution filebotExecution = optFilebotExecution.get();
            filebotExecution.setCategory(filebotExecutionODTO.getCategory());
            filebotExecution.setAction(filebotExecutionODTO.getAction());
            filebotExecution.setForceStrict(filebotExecutionODTO.isForceStrict());
            filebotExecution.setQuery(filebotExecutionODTO.getQuery());
            if (filebotExecution.getCategory().equals(FilebotCategory.TV_EN)) {
                filebotExecution.setEnglish(true);
            }
            filebotExecution
                    .setCommand(filebotUtils.getFilebotCommand(Path.of(filebotExecution.getPath()),
                            filebotExecution.getQuery(), filebotExecution.getCategory(), filebotExecution.isForceStrict(),
                            filebotExecution.isEnglish(), filebotExecution.getAction(), filebotExecution.isOnTestPhase()));
            filebotExecution.setStatus(FilebotExecution.FilebotStatus.PENDING);
            filebotExecutionRepository.save(filebotExecution);
            log.info("FilebotExecution founded and updated: {}", filebotExecutionODTO);

        } else {
            log.error("FilebotExecution not found: {}", filebotExecutionODTO);
        }
    }

    @Override
    public void resolutionTelegramBotTest(FilebotExecutionTestODTO filebotExecutionTestODTO) {
        Optional<FilebotExecution> optFilebotExecution = filebotExecutionRepository
                .findById(filebotExecutionTestODTO.getId());
        if (optFilebotExecution.isPresent()
                && Boolean.FALSE.equals(optFilebotExecution.get().getStatus() == FilebotExecution.FilebotStatus.PROCESSED)) {
            FilebotExecution filebotExecution = optFilebotExecution.get();
            filebotExecution.setOnTestPhase(Boolean.FALSE.equals(filebotExecutionTestODTO.isApproved()));
            filebotExecution.setStatus(FilebotExecution.FilebotStatus.PENDING);
            filebotExecution.setCommand(
                    filebotUtils.getFilebotCommand(Path.of(filebotExecution.getPath()),
                            filebotExecution.getQuery(), filebotExecution.getCategory(), filebotExecution.isForceStrict(),
                            filebotExecution.isEnglish(), filebotExecution.getAction(), filebotExecution.isOnTestPhase()));
            save(filebotExecution);
            log.info("FilebotExecution {} approved and updated: {}", filebotExecutionTestODTO.isApproved(), filebotExecution);
            filebotService.executeByKubernetes(filebotExecution); //FIXME: ADD     @Value("${lavanda.namespace}")
            // private String KUBERNETES_NAMESPACE;
        } else {
            log.error("FilebotExecution not found: {}", filebotExecutionTestODTO.getId());
        }
    }

    @Override
    public FilebotExecution save(FilebotExecution filebotExecution) {
        return filebotExecutionRepository.save(filebotExecution);
    }

    @Override
    public void checkPossiblesNewFilebotExecution() {
        List<String> files = getAllFilesInput();
        for (String file : files) {
            if (filebotExecutionRepository.findByName(file).isPresent()) {
                log.info("Already exists {}", file);
            } else {
                if (fileService.isValidForFilebot(filebotUtils.getFilebotPathInput() + "/" + file)) {
                    log.info("Creating new Execution {}", file);
                    FilebotExecution filebotExecution = new FilebotExecution();
                    filebotExecution.setPath(filebotUtils.getFilebotPathInput() + "/" + file);
                    filebotExecution.setName(file);
                    if (fileService.isDirectory(filebotUtils.getFilebotPathInput() + "/" + file)) {
                        filebotExecution.setFiles(fileService.getFilesExecutor(filebotExecution.getPath()));
                    } else {
                        FilebotExecution.FileExecutor fileExecutor = new FilebotExecution.FileExecutor();
                        fileExecutor.setFile(filebotUtils.getFilebotPathInput() + "/" + file);
                        filebotExecution.setFiles(List.of(fileExecutor));
                    }
                    if (Boolean.FALSE.equals(filebotExecution.getFiles().isEmpty())) {
                        filebotExecution.setStatus(FilebotExecution.FilebotStatus.ON_TELEGRAM);
                        filebotExecution = filebotExecutionRepository.save(filebotExecution);
                        FilebotExecutionIDTO filebotExecutionIDTO = new FilebotExecutionIDTO();
                        filebotExecutionIDTO.setId(filebotExecution.getId());
                        filebotExecutionIDTO.setPath(filebotExecution.getPath());
                        filebotExecutionIDTO.setFiles(filebotExecution.getFiles().stream().map(FilebotExecution.FileExecutor::getFile)
                                .toList());
                        filebotExecutionIDTO.setName(filebotExecution.getName());
                        producerService.sendFilebotExecutionToTelegram(filebotExecutionIDTO);
                    } else {
                        log.info("Path not valid, not contain valid files or is empty{}", file);
                        continue;
                    }
                } else {
                    log.info("File not valid {}", file);
                }

            }
        }
    }

    @Override
    public void createNewExecution(QbittorrentModel qbittorrentModel) {
        log.info("Creating new Execution {}", qbittorrentModel.getName());
        FilebotExecution filebotExecution = new FilebotExecution();
        filebotExecution.setPath(filebotUtils.getFilebotPathInput() + "/" + qbittorrentModel.getName());
        filebotExecution.setName(qbittorrentModel.getName());
        filebotExecution.setStatus(FilebotExecution.FilebotStatus.ON_TELEGRAM);
        filebotExecution = filebotExecutionRepository.save(filebotExecution);

        FilebotExecutionIDTO filebotExecutionIDTO = new FilebotExecutionIDTO();
        filebotExecutionIDTO.setId(filebotExecution.getId());
        filebotExecutionIDTO.setName(filebotExecution.getName());
        filebotExecutionIDTO.setPath(filebotExecution.getPath());
        filebotExecutionIDTO.setFiles(fileService.ls(filebotExecution.getPath()));
        producerService.sendFilebotExecutionToTelegram(filebotExecutionIDTO);
    }

    @Override
    public void delete(String id) {
        FilebotExecution filebotExecution = filebotExecutionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "FilebotExecution not found with the id " + id));
        filebotExecutionRepository.delete(filebotExecution);
    }

    @Override
    public FilebotExecution getById(String id) {
        return filebotExecutionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "FilebotExecution not found with the id " + id));
    }

    @Override
    public FilebotExecution editExecution(String id, FilebotExecution filebotExecution) {
        checkSameId(filebotExecution, id);
        FilebotExecution filebotExecutionEdited = filebotExecutionRepository.findById(id).map(filebotExecutionToEdit -> {
            if (Boolean.FALSE.equals(filebotExecutionToEdit.isManual())) {
                filebotExecutionToEdit.setPath(filebotUtils.getFilebotPathInput() + "/" + filebotExecution.getPath());
            }
            filebotExecutionToEdit.setCategory(filebotExecution.getCategory());
            if (filebotExecution.getCategory().equals(FilebotCategory.TV_EN)) {
                filebotExecutionToEdit.setEnglish(true);
            }
            filebotExecutionToEdit.setAction(filebotExecution.getAction());
            filebotExecutionToEdit.setOnTestPhase(true);
            filebotExecutionToEdit.setCommand(Objects.nonNull(filebotExecution.getCommand()) ? filebotExecution.getCommand()
                    : filebotUtils.getFilebotCommand(Path.of(filebotExecutionToEdit.getPath()), filebotExecutionToEdit.getQuery(),
                    filebotExecutionToEdit.getCategory(), filebotExecutionToEdit.isForceStrict(), filebotExecutionToEdit.isEnglish(), filebotExecution.getAction(), filebotExecutionToEdit.isOnTestPhase()));
            filebotExecutionToEdit.setStatus(FilebotExecution.FilebotStatus.UNPROCESSED);
            return filebotExecutionToEdit;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "FilebotExecution not found with the id " + id));
        return filebotExecutionRepository.save(filebotExecutionEdited);
    }

    private void checkSameId(FilebotExecution filebotExecution, String id) {
        if (!filebotExecution.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The id of the filebotExecution to edit is not the same as the id of the filebotExecution to edit");
        }
    }

    @Override
    public List<String> getAllFiles() {
        return getAllFilesInput();
    }

    private List<String> getAllFilesInput() {
        return fileService.ls(filebotUtils.getFilebotPathInput());
    }

    @Override
    public void forceExecute(String id) {
        Optional<FilebotExecution> optFilebotExecution = filebotExecutionRepository
                .findById(id);
        if (optFilebotExecution.isPresent()) {
            FilebotExecution filebotExecution = optFilebotExecution.get();
            filebotExecution.setCommand(filebotUtils.getFilebotCommand(Path.of(filebotExecution.getPath()),
                    filebotExecution.getQuery(), filebotExecution.getCategory(), filebotExecution.isForceStrict(),
                    filebotExecution.isEnglish(),
                    filebotExecution.getAction(), filebotExecution.isOnTestPhase()));
            save(filebotExecution);
            filebotService.executeByKubernetes(filebotExecution);
        } else {
            log.error("FilebotExecution not found: {}", id);
        }
    }

    @Override
    public void forceExecute() {
        List<FilebotExecution> filebotExecutions = filebotExecutionRepository
                .findByStatusIn(List.of("UNPROCESSED", "PENDING"));
        for (FilebotExecution filebotExecution : filebotExecutions) {
            filebotExecution.setCommand(filebotUtils.getFilebotCommand(Path.of(filebotExecution.getPath()),
                    filebotExecution.getQuery(), filebotExecution.getCategory(), filebotExecution.isForceStrict(),
                    filebotExecution.isEnglish(),
                    filebotExecution.getAction(), filebotExecution.isOnTestPhase()));
            save(filebotExecution);
            filebotService.executeByKubernetes(filebotExecution);
        }
    }

}
