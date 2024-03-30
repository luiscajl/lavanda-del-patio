package es.lavanda.filebot.executor.exception;

import es.lavanda.filebot.executor.model.FilebotCommandExecution;
import lombok.Getter;

@Getter
public class FilebotAMCException extends RuntimeException {

    private Type type;

    private FilebotCommandExecution filebotCommandExecution;

    public FilebotAMCException(Type type, FilebotCommandExecution filebotCommandExecution) {
        this.type = type;
        this.filebotCommandExecution = filebotCommandExecution;
    }

    public enum Type {
        STRICT_QUERY, REGISTER, SELECTED_OPTIONS, FILE_EXIST, FILES_NOT_FOUND, ERROR;
    }
}
