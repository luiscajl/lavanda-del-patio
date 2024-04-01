package es.lavanda.lib.common.model.flaresolverr.input;

import lombok.Data;

@Data
public class FlaresolverrIDTO {

    private String status;
    private String message;

    private SolutionIDTO solution;

    @Data
    public class SolutionIDTO {
        private String response;
        private String url;
        private String status;
    }
}

// {"status":"ok","message":"Challenge not
// detected!","solution":{"url":"https://atomohd.ninja/","status":200,"cookies":[],"userAgent":"Mozilla/5.0
// (X11; Linux aarch64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0
// Safari/537.36","headers":{},"response"