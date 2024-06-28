package es.lavanda.lib.common.model.flaresolverr.output;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.io.Serializable;

@Data
public class FlaresolverrODTO implements Serializable{

    private String cmd;

    private String url;

    private int maxTimeout;
}
