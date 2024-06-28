package es.lavanda.tmdb.service.strategy;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import es.lavanda.lib.common.model.MediaIDTO;
import es.lavanda.lib.common.model.MediaODTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionODTO;
import es.lavanda.lib.common.model.tmdb.search.TMDBResultDTO;
import es.lavanda.lib.common.model.tmdb.search.TMDBSearchDTO;
import es.lavanda.tmdb.model.type.QueueType;
import es.lavanda.tmdb.service.ProducerService;
import es.lavanda.tmdb.service.impl.TMDBServiceFilm;
import es.lavanda.tmdb.util.FileUtils;
import es.lavanda.tmdb.util.TmdbUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TMDBStrategyFilm implements TMDBStrategy {

    // private static final Pattern PATTERN_SHOW_1 =
    // Pattern.compile("(.*?)\\.S(\\d{1,2})");

    @Autowired
    private ProducerService producerService;

    @Autowired
    private TMDBServiceFilm tmdbServiceFilmImpl;

    @Autowired
    private FileUtils fileUtils;

    @Override
    public void execute(MediaIDTO mediaDTO, QueueType type) {
        log.info("On strategy Film with mediaIDTO {}", mediaDTO);
        TMDBSearchDTO searchs = tmdbServiceFilmImpl.searchFilm(mediaDTO.getTorrentCroppedTitle(),
                mediaDTO.getTorrentYear());
        log.info("Results of the search {}", searchs.getResults());
        if (Boolean.FALSE.equals(searchs.getResults().isEmpty())) {
            MediaODTO mediaODTO = createMediaODTO(searchs, mediaDTO);
            log.info("Ready to send Message with mediaODTO ", mediaODTO);
            producerService.sendMessage(mediaODTO, type);
        }

    }

    private static MediaODTO createMediaODTO(TMDBSearchDTO searchs, MediaIDTO mediaIDTO) {
        TMDBResultDTO firstResult = searchs.getResults().get(0);
        MediaODTO mediaODTO = new MediaODTO();
        mediaODTO.setId(mediaIDTO.getId());
        mediaODTO.setTitle(firstResult.getTitle());
        mediaODTO.setTitleOriginal(firstResult.getOriginalTitle());
        mediaODTO.setIdOriginal(String.valueOf(firstResult.getId()));
        mediaODTO.setImage(TmdbUtil.getW780Image(firstResult.getPosterPath()));
        if (StringUtils.hasText(firstResult.getReleaseDate())) {
            mediaODTO.setReleaseDate(
                    LocalDate.parse(firstResult.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        mediaODTO.setBackdropImage(TmdbUtil.getOriginalImage(firstResult.getBackdropPath()));
        mediaODTO.setVoteAverage(firstResult.getVoteAverage());
        mediaODTO.setOverview(firstResult.getOverview());
        return mediaODTO;
    }

    @Override
    public void execute(TelegramFilebotExecutionIDTO telegramFilebotExecutionIDTO) {
        log.info("Strategy Film  with telegramFilebotExecutionIDTO {}", telegramFilebotExecutionIDTO);
        String search = fileUtils.getShortName(telegramFilebotExecutionIDTO.getPath());
        TMDBSearchDTO searchs = tmdbServiceFilmImpl.searchFilm(search);
        log.info("Searched {} with results {}", search, searchs.getResults());
        // if (Boolean.FALSE.equals(searchs.getResults().isEmpty())) {
        TelegramFilebotExecutionODTO telegramFilebotExecutionODTO = createTelegramFilebotExecutionODTO(searchs,
                telegramFilebotExecutionIDTO.getId());
        log.info("Ready to send Message with TelegramFilebotExecutionODTO ", telegramFilebotExecutionODTO);
        producerService.sendMessage(telegramFilebotExecutionODTO, QueueType.TELEGRAM_QUERY_TMDB_RESOLUTION);
        // }
    }

    private TelegramFilebotExecutionODTO createTelegramFilebotExecutionODTO(TMDBSearchDTO searchs, String id) {
        TelegramFilebotExecutionODTO telegramFilebotExecutionODTO = new TelegramFilebotExecutionODTO();
        telegramFilebotExecutionODTO.setId(id);
        Map<String, TMDBResultDTO> possibleChoices = new HashMap<>();
        for (TMDBResultDTO tMDBResultDTO : searchs.getResults()) {
            possibleChoices.put(String.valueOf(tMDBResultDTO.getId()), tMDBResultDTO);
        }
        telegramFilebotExecutionODTO.setPossibleChoices(possibleChoices);
        return telegramFilebotExecutionODTO;
    }

}
