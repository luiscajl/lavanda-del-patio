package es.lavanda.lib.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import es.lavanda.lib.common.model.tmdb.search.TMDBResultDTO;
import lombok.Data;

@Data
public class TelegramFilebotExecutionODTO implements Serializable {

    private String id;

    private Map<String,TMDBResultDTO> possibleChoices= new HashMap<>();

}
