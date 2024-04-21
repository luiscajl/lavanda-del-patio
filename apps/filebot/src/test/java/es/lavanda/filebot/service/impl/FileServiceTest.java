package es.lavanda.filebot.service.impl;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {


    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(fileService, "HOST_PATH_DATA_VOLUME", "/test");
        ReflectionTestUtils.setField(fileService, "USER", "568");
        ReflectionTestUtils.setField(fileService, "KUBERNETES_NAMESPACE", "lavanda");
    }

    @Test
    @Disabled
    public void test() {
        List<String> result = fileService.ls("/media/qbittorrentvpn-data-downloaded/The Walking Dead The Ones Who Live S01E06 The Last Time 1080p AMZN WEB-DL DDP5 1 H 264-NTb[TGx]");
        System.out.println("WAIT");
    }

}
