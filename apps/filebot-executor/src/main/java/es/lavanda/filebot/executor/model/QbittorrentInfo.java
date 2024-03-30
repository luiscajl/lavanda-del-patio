package es.lavanda.filebot.executor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QbittorrentInfo {
    
    @JsonProperty("content_path")
    private String contentPath;

    @JsonProperty("save_path")
    private String savePath;

    private String category;

}
