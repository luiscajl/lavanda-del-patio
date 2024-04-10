package es.lavanda.filebot.executor.service;

import es.lavanda.filebot.executor.model.FilebotExecution;

public interface FilebotService {

    FilebotExecution executeByKubernetes(FilebotExecution filebotExecution);

}