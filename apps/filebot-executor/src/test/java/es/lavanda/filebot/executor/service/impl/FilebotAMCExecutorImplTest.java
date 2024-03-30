package es.lavanda.filebot.executor.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import es.lavanda.filebot.executor.model.FilebotCommandExecution;
import es.lavanda.filebot.executor.service.FilebotAMCExecutor;
import es.lavanda.filebot.executor.service.FilebotService;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class FilebotAMCExecutorImplTest {

    private FilebotAMCExecutor filebotAMCExecutor;

    @Mock
    private ExecutorService executorServiceBean = Executors.newSingleThreadExecutor();

    @BeforeEach
    public void setup() {
        filebotAMCExecutor = new FilebotAMCExecutorImpl(executorServiceBean);
        // ReflectionTestUtils.setField(filebotAMCExecutor, "FILEBOT_PATH",
        // "src/main/resources/filebot/");
        // ReflectionTestUtils.setField(filebotAMCExecutor, "FILEBOT_OUTPUT_PATH",
        // "src/main/resources/filebot_output/");

    }

    @Test
    @SneakyThrows
    @Disabled
    public void test() {
        String command = "filebot -script 'fn:amc'  --output \"/Users/luiscarlos/Documents/Github/LavandaDelPatio/filebot-executor/src/main/resources/filebot_output/\"  --action move  --def movieDB=TheMovieDB seriesDB=TheMovieDB::TV animeDB=AniDB musicDB=ID3  --lang en  --order Airdate  -no-xattr \"/Users/luiscarlos/Documents/Github/LavandaDelPatio/filebot-executor/src/main/resources/filebot/NoFile\" --def  'movieFormat=/media/Peliculas/{n} ({y})/{n} ({y})_{audioLanguages}_{vf}_{bitrate}'  'seriesFormat=/media/Series-EN/{n}/ Season {s}/{n} s{s.pad(2)}e{e.pad(2)}_{audioLanguages}_{vf}_{bitrate}'  --def 'storeReport=/home/filebot/.reports'  'unsortedFormat=/media/Unsorted/{fn}.{ext}'  --q \"71912\"  --def \"ut_label=tv\"";
        FilebotCommandExecution log = filebotAMCExecutor.execute(command);

    }

}
