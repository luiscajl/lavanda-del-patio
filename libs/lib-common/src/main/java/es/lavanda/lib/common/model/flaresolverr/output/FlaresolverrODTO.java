package es.lavanda.lib.common.model.flaresolverr.output;

import lombok.Data;

@Data
public class FlaresolverrODTO {
    private String cmd;
    private String url;
    private int maxTimeout;
}
