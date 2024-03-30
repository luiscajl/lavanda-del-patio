package es.lavanda.filebot.executor.service.impl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import es.lavanda.filebot.executor.exception.FilebotAMCException;
import es.lavanda.filebot.executor.exception.FilebotAMCException.Type;
import es.lavanda.filebot.executor.exception.FilebotExecutorException;
import es.lavanda.filebot.executor.model.FilebotCommandExecution;
import es.lavanda.filebot.executor.service.FilebotAMCExecutor;
import es.lavanda.filebot.executor.util.StreamGobbler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilebotAMCExecutorImpl implements FilebotAMCExecutor {

    private final ExecutorService executorService;

    private static final Pattern PATTERN_PROCESSED_FILE = Pattern.compile("Processed \\d+ file");

    private static final Pattern PATTERN_FILES_NOT_FOUND = Pattern.compile("Exit status: 100");

    private static final Pattern PATTERN_NOT_FILE_SELECTED = Pattern.compile("No files selected for processing");

    private static final Pattern PATTERN_MOVED_CONTENT = Pattern.compile("\\[(.*)\\] from \\[(.*)\\] to \\[(.*)\\]");

    private static final Pattern PATTERN_SKIPPED_CONTENT = Pattern
            .compile("\\[(.*)\\] Skipped \\[(.*)\\] because \\[(.*)\\] already exists");

    @Override
    public FilebotCommandExecution execute(String command) {
        FilebotCommandExecution execution = filebotExecution(command);
        isNotLicensed(execution);
        isNonStrictOrQuery(execution);
        isNoFilesSelectedOrSkipped(execution);
        isChooseOptions(execution);
        isFileExists(execution);
        isError(execution);
        return execution;
    }

    private FilebotCommandExecution filebotExecution(String command) {
        Process process = null;
        StringBuilder sbuilder = new StringBuilder();
        try {
            process = new ProcessBuilder("bash", "-c", command).redirectErrorStream(true).start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), line -> {
                log.info("Filebot commandLine: {}", line);
                sbuilder.append(line).append("\n");
            });
            executorService.submit(streamGobbler);
            int exitStatus = process.waitFor();
            String logResult = sbuilder.toString();

            FilebotCommandExecution filebotCommandExecution = new FilebotCommandExecution();
            filebotCommandExecution.setExitStatus(exitStatus);
            filebotCommandExecution.setLog(logResult);
            log.info("Exit status: {}", filebotCommandExecution.getExitStatus());
            return filebotCommandExecution;
        } catch (InterruptedException | IOException e) {
            log.error("Exception on Filebot commandLine", e);
            Thread.currentThread().interrupt();
            throw new FilebotExecutorException("Exception on Filebot commandLine", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private void isNotLicensed(FilebotCommandExecution execution) {
        log.debug("Checking if is licensed");
        if (execution.getLog().contains("License Error: UNREGISTERED")) {
            throw new FilebotAMCException(Type.REGISTER, execution);
        }
    }

    private void isNoFilesSelectedOrSkipped(FilebotCommandExecution execution) {
        Matcher matcherNoFilesSelected = PATTERN_NOT_FILE_SELECTED.matcher(execution.getLog());
        Matcher matcherFilesNotFound = PATTERN_FILES_NOT_FOUND.matcher(execution.getLog());
        Matcher matcherMovedContent = PATTERN_MOVED_CONTENT.matcher(execution.getLog());
        Matcher matcherSkippedContent = PATTERN_SKIPPED_CONTENT.matcher(execution.getLog());
        if ((execution.getExitStatus() == 100 || matcherNoFilesSelected.find() || matcherFilesNotFound.find())
                && Boolean.FALSE.equals(matcherMovedContent.find())
                && Boolean.FALSE.equals(matcherSkippedContent.find())) {
            throw new FilebotAMCException(Type.FILES_NOT_FOUND, execution);
        }
    }

    private void isNonStrictOrQuery(FilebotCommandExecution execution) {
        if (execution.getLog().contains("Consider using -non-strict to enable opportunistic matching")
                || execution.getLog().contains("No episode data found:")
                || (notContainsProcessedFile(execution)
                        && execution.getLog().contains("Finished without processing any files"))) {
            throw new FilebotAMCException(Type.STRICT_QUERY, execution);
        }
    }

    private void isError(FilebotCommandExecution execution) {
        if (execution.getExitStatus() == 3) {
            throw new FilebotAMCException(Type.ERROR, execution);
        }
    }

    private boolean notContainsProcessedFile(FilebotCommandExecution execution) {
        Matcher matcherMovedContent = PATTERN_PROCESSED_FILE.matcher(execution.getLog());
        if (!matcherMovedContent.find()) {
            return true;
        }
        return false;
    }

    private void isFileExists(FilebotCommandExecution execution) {
        Matcher matcherMovedContent = PATTERN_SKIPPED_CONTENT.matcher(execution.getLog());
        if (matcherMovedContent.find()) {
            throw new FilebotAMCException(Type.FILE_EXIST, execution);
        }
    }

    private void isChooseOptions(FilebotCommandExecution execution) {
        if (execution.getLog().contains("XXXX")) {
            throw new FilebotAMCException(Type.SELECTED_OPTIONS, execution);
        }
    }

}
