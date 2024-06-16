package es.lavanda.lib.common.model.flaresolverr.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlaresolverrIDTO {

    private String status;
    private String message;
    private SolutionIDTO solution;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class SolutionIDTO {
        private String response;
        private String url;
        private String status;
    }
}