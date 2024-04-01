package es.lavanda.lib.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, exclude = "torrentYear")
public class FilmModelTorrent extends TorrentModel {

    private int torrentYear;

}
