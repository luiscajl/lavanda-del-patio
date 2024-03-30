package es.lavanda.filebot.executor.service;

import java.io.File;
import java.util.List;

import es.lavanda.filebot.executor.model.FilebotExecution.FileExecutor;

public interface FileService {

    List<String> ls(String path);

    void rmdir(String path);

    boolean isValidForFilebot(String fileOrDirectory);

    List<FileExecutor> getFilesExecutor(String path);

    boolean isDirectory(String file);
}
