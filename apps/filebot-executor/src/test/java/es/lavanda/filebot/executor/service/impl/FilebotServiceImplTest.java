package es.lavanda.filebot.executor.service.impl;

import com.oracle.svm.core.annotate.Inject;
import es.lavanda.filebot.executor.model.FilebotCommandExecution;
import es.lavanda.filebot.executor.model.FilebotExecution;
import es.lavanda.filebot.executor.repository.FilebotExecutionRepository;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Profile;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ExtendWith(MockitoExtension.class)
public class FilebotServiceImplTest {

    @Mock
    private ExecutorService executorServiceBean = Executors.newSingleThreadExecutor();

    @Mock
    private FilebotExecutionRepository filebotExecutionRepository;

    @InjectMocks
    private FilebotServiceImpl filebotService;


    @Test
    @Disabled
    public void test() {
        String command = "filebot -script 'fn:amc'  --output \"/media\"  --action test  --def movieDB=TheMovieDB seriesDB=TheMovieDB::TV animeDB=AniDB musicDB=ID3  --lang en  --order Airdate  -no-xattr \"/media/qbittorrentvpn-data-downloaded/The Walking Dead The Ones Who Live S01E06 The Last Time 1080p AMZN WEB-DL DDP5 1 H 264-NTb[TGx]\" --def  'movieFormat=/media/Peliculas/{n} ({y})/{n} ({y})_{audioLanguages}_{vf}_{bitrate}'  'seriesFormat=/media/Series-EN/{n}/ Season {s}/{n} s{s.pad(2)}e{e.pad(2)}_{audioLanguages}_{vf}_{bitrate}'  --def 'storeReport=/home/apps/.reports'  'unsortedFormat=/media/Unsorted/{fn}.{ext}'  --q \"206586\"  --def \"ut_label=SERIES\" ";
        FilebotExecution filebot = new FilebotExecution();
//        command = "sleep 300000000";
        filebot.setCommand(command);
        filebot.setName("The Walking Dead The Ones Who Live S01E06 The Last Time 1080p AMZN WEB-DL DDP5 1 H 264-NTb[TGx]");
        FilebotExecution result = filebotService.executeByKubernetes(filebot);
        System.out.println("WAIT");
    }
}
