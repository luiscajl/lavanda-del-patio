package es.lavanda.lib.common.model.bt4g;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class Bt4g {
    private String name;

    private String domain;

    // private String year;

    private String url;

    private String image;

    private String fullName;

    private String category;

    private String magnet;

    private String magnetHash;

    private int seeders;

    private int leechers;

    private String size;

    private LocalDate createDate;

    private List<String> files;

    private int numFiles;
}
