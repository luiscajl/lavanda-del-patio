package es.lavanda.tmdb.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.lavanda.lib.common.model.tmdb.search.TMDBSearchDTO;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class TMDBUtilTest {

    @Test
    @SneakyThrows
    // @Disabled ("Tarda mucho en CI")
    public void test() {
        // filmModel.setCleanTitle("DEATH PROOF");
        TMDBSearchDTO result = TmdbUtil.multiSearch("DEATH PROOF");
        Assertions.assertNotNull(result);

    }
}
