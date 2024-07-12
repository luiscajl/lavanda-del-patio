package es.lavanda.lib.common.model.bt4g;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class Bt4gDTO {

    private String id;

    private String name;

    private String url;

    private String magnet;

    private String magnetHash;

    private Date createTime;

    private String fileSize;

    private int seeders;

    private int leechers;

    private boolean downloaded;

    private String updated;

    private List<String> files;

}
