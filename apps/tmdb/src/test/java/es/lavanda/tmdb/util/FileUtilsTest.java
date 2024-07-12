package es.lavanda.tmdb.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FileUtilsTest {

    @Autowired
    private FileUtils fileUtils;

    @Test
    public void firstBracketsReturnName() {
        String result = fileUtils
                .getShortName("Supernatural (2005) Season 9 S09 (1080p BluRay x265 HEVC 10bit AAC 5.1 Silence)");
        assertEquals("Supernatural", result.trim());
    }

    @Test
    public void firstSquareReturnName() {
        String result = fileUtils
                .getShortName("El incidente [BluRay 1080p][DTS 5.1 Castellano DTS-HD 5.1-Ingles+Subs][ES-EN]");
        assertEquals("El incidente", result.trim());
    }

    @Test
    public void firstSAndDigitsReturnName() {
        String result = fileUtils
                .getShortName("Selena.plus.Chef.S03.COMPLETE.720p.HMAX.WEBRip.x264-GalaxyTV[TGx]");
        assertEquals("Selena plus Chef", result.trim());
    }

    @Test
    public void firstSlashReturnName() {
        String result = fileUtils
                .getShortName("La Casa del Dragon - Temporada 1 [HDTV 720p][Cap.10  9][AC3 5.1 Castellano][www.atomoHD.vet]");
        assertEquals("La Casa del Dragon", result.trim());
    }
    @Test
    public void firstBracketsReturnName2() {
        String result = fileUtils
                .getShortName("Supernatural (2005) Season 3 S03 + Extras (1080p BluRay x265 HEVC 10bit AAC 5.1 RCVR)");
        assertEquals("Supernatural", result.trim());
    }
    @Test
    public void firstBracketsMovieReturnName() {
        String result = fileUtils
                .getShortName("Para toda la vida (2021) [BluRay 720p X264 MKV][AC3 5.1 Castellano][PctReload1.com]");
        assertEquals("Para toda la vida", result.trim());
    }
    @Test
    public void firstBracketsReturnName3() {
        String result = fileUtils
                .getShortName("Supernatural (2005) Season 2 S02 + Extras (1080p BluRay x265 HEVC 10bit AAC 5.1 RCVR)");
        assertEquals("Supernatural", result.trim());
    }
    @Test
    public void firstBracketsReturnName4() {
        String result = fileUtils
                .getShortName("Supernatural (2005) Season 10 S10 (1080p BluRay x265 HEVC 10bit AAC 5.1 Silence)");
        assertEquals("Supernatural", result.trim());
    }
    @Test
    public void firstSlashReturnName5() {
        String result = fileUtils
                .getShortName("Supernatural (2005) Season 8 S08 (1080p BluRay x265 HEVC 10bit AAC 5.1 Silence)");
        assertEquals("Supernatural", result.trim());
    }
    @Test
    public void firstSlashReturnName6() {
        String result = fileUtils
                .getShortName("Supernatural (2005) Season 5 S05 + Extras (1080p BluRay x265 HEVC 10bit AAC 5.1 RCVR)");
        assertEquals("Supernatural", result.trim());
    }


}
