package es.lavanda.tmdb.service.strategy;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.lavanda.lib.common.model.MediaIDTO.Type;
import es.lavanda.lib.common.model.tmdb.search.TMDBResultDTO;
import es.lavanda.lib.common.model.tmdb.search.TMDBSearchDTO;
import es.lavanda.lib.common.model.tmdb.search.TMDBResultDTO.MediaTypeEnum;
import es.lavanda.tmdb.util.TmdbUtil;

@ExtendWith(MockitoExtension.class)
public class TMDBStrategyFactoryTest {

    @InjectMocks
    private TMDBStrategyFactory tmdbStrategy = new TMDBStrategyFactoryImpl();

    @Mock
    private TMDBStrategyFilm tmdbStrategyFilm;

    @Mock
    private TMDBStrategyShow tmdbStrategyShow;

    @Test
    public void getStrategyWithException() {
        Assertions.assertThrows(NullPointerException.class, () -> tmdbStrategy.getFactory(null, null));
    }

    @Test
    @Disabled
    public void getStrategyWithTypeShow() {
        TMDBSearchDTO searchDTO = new TMDBSearchDTO();
        List<TMDBResultDTO> results = new ArrayList<>();
        TMDBResultDTO tmdb = new TMDBResultDTO();
        tmdb.setMediaType(MediaTypeEnum.MOVIE);
        results.add(tmdb);
        searchDTO.setResults(results);
        Optional<TMDBStrategy> strategy = tmdbStrategy.getFactory("wonderwoman", Type.SHOW);
        Assertions.assertEquals(tmdbStrategyShow, strategy.get());
    }

    @Test
    @Disabled

    public void getStrategyWithTypeFilmAndCapitulo() {
        TMDBSearchDTO searchDTO = new TMDBSearchDTO();
        List<TMDBResultDTO> results = new ArrayList<>();
        TMDBResultDTO tmdb = new TMDBResultDTO();
        tmdb.setMediaType(MediaTypeEnum.TV);
        results.add(tmdb);
        searchDTO.setResults(results);
        when(TmdbUtil.multiSearch("wonderwoman")).thenReturn(searchDTO);
        Optional<TMDBStrategy> strategy = tmdbStrategy.getFactory("wonderwoman", Type.FILM);
        Assertions.assertEquals(tmdbStrategyShow, strategy.get());
    }

    @Test
    @Disabled
    public void getStrategyWithTypeFilmAndNotCapitulo() {
        TMDBSearchDTO searchDTO = new TMDBSearchDTO();
        List<TMDBResultDTO> results = new ArrayList<>();
        TMDBResultDTO tmdb = new TMDBResultDTO();
        tmdb.setMediaType(MediaTypeEnum.MOVIE);
        results.add(tmdb);
        searchDTO.setResults(results);
        when(TmdbUtil.multiSearch("wonderwoman")).thenReturn(searchDTO);
        Optional<TMDBStrategy> strategy = tmdbStrategy.getFactory("wonderwoman", Type.FILM);
        Assertions.assertEquals(tmdbStrategyFilm, strategy.get());
    }
}
