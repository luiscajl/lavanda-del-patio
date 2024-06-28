package es.lavanda.lib.common.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class FilebotExecutionTestIDTO implements Serializable {

    private String id;

    private String path;

    private String name;

    private List<String> files = new ArrayList<>();

    private List<String> possibilities = new ArrayList<>();

}
