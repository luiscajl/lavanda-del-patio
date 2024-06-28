package es.lavanda.downloader.bt4g.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document("bt4g")
@ToString
public class Bt4g {

    @Id
    private String id;

    private String name;

    private String url;

    private String magnet;

    @Indexed(unique = true)
    private String magnetHash;

    private Date createTime;

    private String fileSize;

    private int seeders;

    private int leechers;

    private boolean downloaded;

    private String updated;

    private List<String> files;
}
