package es.lavanda.downloader.bt4g.model;


import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("magnets")
@ToString
public class Magnet {
    @Id
    private String id;

    private String url;
}
