package es.lavanda.lib.common.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class ShowModelTorrent extends TorrentModel {

    private Map<Integer, List<Integer>> torrentSeasonsChapters = new HashMap<>();

    private int season;

    private List<Integer> chapters;

    private Date torrentDate;


}
