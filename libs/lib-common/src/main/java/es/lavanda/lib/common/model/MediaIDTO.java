package es.lavanda.lib.common.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class MediaIDTO implements Serializable {

    private static final long serialVersionUID = -5766945695641205660L;

    private String id;

    private String torrentTitle;

    private String torrentCroppedTitle;

    private Integer torrentYear;

    private Type possibleType;

    private Type type;

    public enum Type {
        FILM, SHOW;
    }

}
