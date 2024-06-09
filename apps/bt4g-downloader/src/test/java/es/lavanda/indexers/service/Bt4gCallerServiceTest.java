package es.lavanda.indexers.service;

import es.lavanda.downloader.bt4g.model.Bt4g;
import es.lavanda.downloader.bt4g.service.Bt4gCallerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.SneakyThrows;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class Bt4gCallerServiceTest {

    @InjectMocks
    private Bt4gCallerService bt4gCallerService;

    @Test
    @SneakyThrows
    @Disabled
    public void test() {
        List<Bt4g> result = Assertions.assertDoesNotThrow(() -> bt4gCallerService.callToBT4G("chicagoPd"));
        Assertions.assertNotNull(result);
    }

}
