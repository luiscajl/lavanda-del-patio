package es.lavanda.tmdb.service.strategy;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;

// @ExtendWith(SpringExtension.class)
// @SpringBootTest
// @AutoConfigureMockMvc
public class TMDBStrategyShowTest {

    // @Autowired
    private TMDBStrategyShow tmdbStrategyShow;

    // @Test
    // @Disabled
    public void getStrategyWithTypeShow() {
        TelegramFilebotExecutionIDTO telegramFilebotExecutionIDTO = new TelegramFilebotExecutionIDTO();
        telegramFilebotExecutionIDTO.setFile("Supernatural (2005) Season 9 S09 (1080p BluRay x265 HEVC 10bit AAC 5.1 Silence)");
        telegramFilebotExecutionIDTO.setPath(
                "/Users/luiscarlos/Documents/Github/LavandaDelPatio/filebot-executor/src/main/resources/filebot/Selena.plus.Chef.S03.COMPLETE.720p.HMAX.WEBRip.x264-GalaxyTV[TGx]");
        tmdbStrategyShow.execute(telegramFilebotExecutionIDTO);
        // TMDBSearchDTO searchDTO = new TMDBSearchDTO();
        // List<TMDBResultDTO> results = new ArrayList<>();
        // TMDBResultDTO tmdb = new TMDBResultDTO();
        // tmdb.setMediaType(MediaTypeEnum.MOVIE);
        // results.add(tmdb);
        // searchDTO.setResults(results);
        // Optional<TMDBStrategy> strategy = tmdbStrategy.getFactory("wonderwoman",
        // Type.SHOW);
        // Assertions.assertEquals(tmdbStrategyShow, strategy.get());
    }



}
