package es.lavanda.filebot.executor.model;

import java.io.Serializable;

import es.lavanda.lib.common.model.filebot.FilebotAction;
import es.lavanda.lib.common.model.filebot.FilebotCategory;
import lombok.Data;

@Data
public class QbittorrentModel implements Serializable {

    private String id;

    private String name;

    private FilebotAction action;

    private FilebotCategory category;

}
