// package es.lavanda.tmdb.service;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.doNothing;
// import static org.mockito.Mockito.when;

// import es.lavanda.lib.common.model.MediaIDTO;
// import es.lavanda.lib.common.model.MediaIDTO.Type;
// import es.lavanda.tmdb.service.impl.TMDBServiceImpl;
// import es.lavanda.tmdb.service.strategy.TMDBStrategy;
// import es.lavanda.tmdb.service.strategy.TMDBStrategyFactory;
// import es.lavanda.tmdb.service.strategy.TMDBStrategyFilm;
// import es.lavanda.tmdb.service.strategy.TMDBStrategyShow;

// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Disabled;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import lombok.SneakyThrows;

// @ExtendWith(MockitoExtension.class)
// public class TmdbServiceTest {

//     @InjectMocks
//     private TMDBService tmdbServiceImpl = new TMDBServiceImpl();


//     @Mock
//     private ProducerService producerService;

//     @Mock
//     private TMDBStrategyFactory tmdbStrategy;

//     @Mock
//     private TMDBStrategyShow tmdbStrategyShow;

//     @Mock
//     private TMDBStrategyFilm tmdbStrategyFilm;

//     @Test
//     public void testByYear0() {
//         Assertions.assertNotNull(tmdbServiceImpl);
//         // doNothing().when(producerService).sendFilm(any());
//         when(tmdbStrategy.getFactory("La Liga de la Justicia de Zack Snyder en 4K", Type.SHOW)).thenReturn(tmdbStrategyShow);
//         MediaIDTO filmModel = new MediaIDTO();
//         filmModel.setId("1234");
//         filmModel.setPossibleType(Type.SHOW);
//         filmModel.setTorrentCroppedTitle("La Liga de la Justicia de Zack Snyder en 4K");
//         filmModel.setTorrentTitle("La Liga de la Justicia de Zack Snyder en 4K Año 2021 BDremux-1080p ");
//         filmModel.setTorrentYear(0);

//         Assertions.assertDoesNotThrow(() -> tmdbServiceImpl.analyze(filmModel));
//     }

// }
