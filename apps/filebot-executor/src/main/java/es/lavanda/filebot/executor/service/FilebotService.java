package es.lavanda.filebot.executor.service;

import es.lavanda.filebot.executor.model.FilebotExecution;

public interface FilebotService {

    void execute(FilebotExecution filebotExecution);

}