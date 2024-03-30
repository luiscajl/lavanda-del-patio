package es.lavanda.tmdb.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import es.lavanda.lib.common.model.MediaIDTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;
import es.lavanda.tmdb.model.type.QueueType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {

    private final TMDBService tmdbServiceImpl;

    @RabbitListener(queues = "agent-tmdb-feed-films")
    public void consumeMessageFeedFilms(MediaIDTO mediaDTO) {
        log.debug("Reading message of the queue agent-tmdb-feed-films: {}", mediaDTO);
        tmdbServiceImpl.analyze(mediaDTO, QueueType.FEED_FILMS_RESOLUTION);
        log.debug("Work message finished");
    }

    @RabbitListener(queues = "agent-tmdb-feed-shows")
    public void consumeMessageFeedShows(MediaIDTO mediaDTO) {
        log.debug("Reading message of the queue agent-tmdb: {}", mediaDTO);
        tmdbServiceImpl.analyze(mediaDTO, QueueType.FEED_SHOWS_RESOLUTION);
        log.debug("Work message finished");
    }

    @RabbitListener(queues = "telegram-query-tmdb")
    public void getChoicesOfFile(TelegramFilebotExecutionIDTO telegramFilebotExecutionIDTO) {
        log.debug("Reading message of the queue telegram-query-tmdb: {}", telegramFilebotExecutionIDTO);
        tmdbServiceImpl.analyze(telegramFilebotExecutionIDTO);
        log.debug("Work message finished");
    }

}
