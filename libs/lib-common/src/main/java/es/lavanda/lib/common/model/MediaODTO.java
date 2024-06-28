package es.lavanda.lib.common.model;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;

@Data
public class MediaODTO implements Serializable {

    private static final long serialVersionUID = -5766945695641205660L;

    private String id;

    private String idOriginal;

    private String title;

    private String titleOriginal;

    private String image;

    private String backdropImage;

    private String overview;

    private float voteAverage;

    private LocalDate releaseDate;
    
}
