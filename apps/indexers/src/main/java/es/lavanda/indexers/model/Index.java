package es.lavanda.indexers.model;

import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("index-wolfmax4k")
public class Index {
    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String indexName;

    private String domain;

    private String url;

    private String image;//ImageOnBase64

    private Date createTime;

    private Type type;

    private Quality quality;

    public enum Type {
        TV_SHOW, FILM
    }

    public enum Quality {
        HD, FULL_HD, ULTRA_HD
    }

    @Override
    public String toString() {
        return "Index{" +
                "indexName='" + indexName + '\'' +
                '}';
    }
}
