package es.lavanda.lib.common.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class FilebotExecutionTestODTO implements Serializable {

    private String id;

    private boolean approved;
}
