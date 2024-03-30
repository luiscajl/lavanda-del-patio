package es.lavanda.filebot.executor.service;

import es.lavanda.filebot.executor.model.FilebotCommandExecution;

public interface FilebotAMCExecutor {

    FilebotCommandExecution execute(String command);

}
