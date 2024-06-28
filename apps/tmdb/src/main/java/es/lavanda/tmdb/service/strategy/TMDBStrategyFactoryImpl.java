package es.lavanda.tmdb.service.strategy;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.lavanda.lib.common.model.MediaIDTO.Type;
import es.lavanda.lib.common.model.tmdb.search.TMDBSearchDTO;
import es.lavanda.lib.common.model.tmdb.search.TMDBResultDTO.MediaTypeEnum;
import es.lavanda.tmdb.util.TmdbUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TMDBStrategyFactoryImpl implements TMDBStrategyFactory {

    @Autowired
    private TMDBStrategyFilm tmdbStrategyFilm;

    @Autowired
    private TMDBStrategyShow tmdbStrategyShow;

    @Override
    public Optional<TMDBStrategy> getFactory(String torrentTitle, Type type) {
        if (type.equals(Type.FILM) && MediaTypeEnum.MOVIE.equals(getType(torrentTitle))) {
            log.info("Strategy FILM");
            return Optional.of(tmdbStrategyFilm);
        } else if (Type.SHOW.equals(type)) {
            log.info("Strategy SHOW");
            return Optional.of(tmdbStrategyShow);
        } else {
            log.info("Not strategy selected");
            return Optional.empty();
        }
    }

    private MediaTypeEnum getType(String croppedTitle) {
        TMDBSearchDTO result = TmdbUtil.multiSearch(croppedTitle);
        if (Boolean.FALSE.equals(result.getResults().isEmpty())) {
            return result.getResults().get(0).getMediaType();
        } else {
            // TODO: send SNS message
            log.error("Not found results for {} on tmdb", croppedTitle);
            // throw new TMDBException("Not found anything on tmdb for " + croppedTitle);
            return null;
        }
    }

}
