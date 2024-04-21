package es.lavanda.filebot.service;

import es.lavanda.filebot.model.FilebotExecution;

public interface FilebotService {

    FilebotExecution executeByKubernetes(FilebotExecution filebotExecution);

}