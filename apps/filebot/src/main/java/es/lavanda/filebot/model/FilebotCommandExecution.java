package es.lavanda.filebot.model;

import lombok.Data;

@Data
public class FilebotCommandExecution {

    private int exitStatus;

    private String log;
}
