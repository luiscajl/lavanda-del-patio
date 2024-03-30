package es.lavanda.tmdb.service;

import es.lavanda.lib.common.model.MediaIDTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;
import es.lavanda.tmdb.exception.TMDBException;
import es.lavanda.tmdb.model.type.QueueType;

public interface TMDBService {
    
    void analyze(MediaIDTO mediaDTO,QueueType type) throws TMDBException;

    void analyze(TelegramFilebotExecutionIDTO telegramFilebotExecutionIDTO) throws TMDBException;

}
