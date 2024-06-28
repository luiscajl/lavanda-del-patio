package es.lavanda.filebot.service;

import java.util.List;

import es.lavanda.filebot.model.FilebotExecution;

public interface FileService {

    List<String> ls(String path);

//    void rmdir(String path);

    boolean isValidForFilebot(String fileOrDirectory);

    List<FilebotExecution.FileExecutor> getFilesExecutor(String path);

    boolean isDirectory(String file);
}
