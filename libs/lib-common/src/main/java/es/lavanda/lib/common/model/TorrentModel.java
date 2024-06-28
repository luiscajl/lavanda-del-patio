package es.lavanda.lib.common.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = { "torrentUrl" })
@ToString
public class TorrentModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String torrentId = UUID.randomUUID().toString();

    private String torrentTitle;

    private String torrentCroppedTitle;

    private String torrentImage;

    private String torrentQuality;

    private boolean downloaded;

    private String torrentSize;

    private String torrentUrl;

    private String torrentMagnet;

    private boolean torrentValidate;

    private Page torrentPage;

    public enum Page {
        DON_TORRENT, PCTMIX, PCTFENIX;

    }
}
