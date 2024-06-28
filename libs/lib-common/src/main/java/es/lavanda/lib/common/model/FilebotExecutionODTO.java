package es.lavanda.lib.common.model;

import java.io.Serializable;

import es.lavanda.lib.common.model.filebot.FilebotAction;
import es.lavanda.lib.common.model.filebot.FilebotCategory;
import lombok.Data;

@Data
public class FilebotExecutionODTO implements Serializable {

    private String id;

    private boolean forceStrict; // FORCE_STRICT

    private String query;

    private String selectedPossibilities; // CHOICE

    private FilebotAction action; // ACTION

    private FilebotCategory category; // CATEGORY

}
