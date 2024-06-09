package es.lavanda.downloader.bt4g.model;


import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("search-bt4g")
@ToString
public class Search {
    @Id
    private String id;

    private String name;

    private boolean finished;
}
