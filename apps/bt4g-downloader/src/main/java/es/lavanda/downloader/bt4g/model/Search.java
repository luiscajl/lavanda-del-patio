package es.lavanda.downloader.bt4g.model;


import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("search-bt4g")
@ToString
public class Search {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private List<String> bt4gIds;

    private boolean finished;
}
