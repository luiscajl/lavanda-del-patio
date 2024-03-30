package es.lavanda.filebot.executor.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.lavanda.filebot.executor.exception.FilebotExecutorException;
import es.lavanda.filebot.executor.model.FilebotExecution.FileExecutor;
import es.lavanda.filebot.executor.service.FileService;
import es.lavanda.filebot.executor.util.FilebotUtils;
import es.lavanda.filebot.executor.util.StreamGobbler;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private FilebotUtils filebotUtils;

    @Override
    public List<String> ls(String path) {
        int status;
        List<String> lsResult = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("bash", "-c", "ls \"" + path + "\"").redirectErrorStream(true)
                    .start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), line -> {
                // log.info("BASH commandline: {}", line);
                lsResult.add(line);
            });
            executorService.submit(streamGobbler);
            status = process.waitFor();
            if (status != 0) {
                log.error("LS command result on fail {}", lsResult.stream()
                        .map(n -> String.valueOf(n))
                        .collect(Collectors.joining("\n", "IN-", "-OUT")));
            } else {
                log.debug("LS command result on success {}", lsResult.stream()
                        .map(n -> String.valueOf(n))
                        .collect(Collectors.joining("\n", "IN-", "-OUT")));
            }
            return lsResult;
        } catch (InterruptedException | IOException e) {
            log.error("Exception on command 'LS'", e);
            Thread.currentThread().interrupt();
            throw new FilebotExecutorException("Exception on command 'LS'", e);
        }
    }

    @Override
    public void rmdir(String path) {
        int status;
        List<String> lsResult = new ArrayList<>();
        try {
            Process process = new ProcessBuilder("bash", "-c", "rmdir -r\"" + path + "\"").redirectErrorStream(true)
                    .start();
            StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), line -> {
                // log.info("BASH commandline: {}", line);
                lsResult.add(line);
            });
            executorService.submit(streamGobbler);
            status = process.waitFor();
            if (status != 0) {
                log.error("RMDIR command result on fail {}", lsResult.stream()
                        .map(n -> String.valueOf(n))
                        .collect(Collectors.joining("\n", "IN-", "-OUT")));
            } else {
                log.debug("RMDIR command result on success {}", lsResult.stream()
                        .map(n -> String.valueOf(n))
                        .collect(Collectors.joining("\n", "IN-", "-OUT")));
            }
        } catch (InterruptedException | IOException e) {
            log.error("Exception on command 'RMDIR'", e);
            Thread.currentThread().interrupt();
            throw new FilebotExecutorException("Exception on command 'RMDIR'", e);
        }
    }

    private boolean isValidExtension(String extension) {
        System.out.println("La extensión del archivo es: " + extension);
        if (extension.equalsIgnoreCase("mkv") || extension.equalsIgnoreCase("mp4")
                || extension.equalsIgnoreCase("avi")) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isValidForFilebot(String fileOrDirectory) {
        File file = new File(fileOrDirectory);
        if (!file.exists()) {
            throw new FilebotExecutorException(String.format("File not found %s", fileOrDirectory));
        }
        if (file.isFile()) {
            log.info("Is a file {}", fileOrDirectory);
            return isValidExtension(getFileExtension(file));
            // boolean valid = isValidExtension(getFileExtension(file));
            // if (valid) {
            //     return valid;
            // } else {
            //     return isRarFile(getFileExtension(file));
            // }
        } else if (file.isDirectory()) {
            log.info("Is a directory {}", fileOrDirectory);
            return true;
        } else {
            return false;
        }
    }

    // Método para obtener la extensión de un archivo
    public static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    @Override
    public List<FileExecutor> getFilesExecutor(String path) {
        List<FileExecutor> files = new ArrayList<>();
        List<String> ls = ls(path);
        for (String file : ls) {
            if (isValidForFilebot(path + "/" + file)) {
                FileExecutor fileExecutor = new FileExecutor();
                fileExecutor.setFile(file);
                fileExecutor.setNewFile(null);
                files.add(fileExecutor);
            }
        }
        return files;
    }

    @Override
    public boolean isDirectory(String fileOrDirectory) {
        File file = new File(fileOrDirectory);
        return file.isDirectory();
    }
}
