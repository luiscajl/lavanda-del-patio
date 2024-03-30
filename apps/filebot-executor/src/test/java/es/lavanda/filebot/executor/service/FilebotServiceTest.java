package es.lavanda.filebot.executor.service;

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

import es.lavanda.filebot.executor.service.impl.FilebotServiceImpl;

@ExtendWith(MockitoExtension.class)
public class FilebotServiceTest {

    @InjectMocks
    private FilebotService filebotService = new FilebotServiceImpl();
    
    @Mock
    private ExecutorService executorServiceBean = Executors.newSingleThreadExecutor();

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(filebotService, "FILEBOT_PATH", "src/main/resources/filebot/");
        ReflectionTestUtils.setField(filebotService, "FILEBOT_OUTPUT_PATH", "src/main/resources/filebot_output/");

    }

    @Disabled
    @Test
    public void test() {
        // filebotService.execute();
    }
}
