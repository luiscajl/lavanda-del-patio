package es.lavanda.indexers.service;

import es.lavanda.indexers.model.Index;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import lombok.SneakyThrows;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class Wolfmax4kCallerServiceTest {

    @InjectMocks
    private Wolfmax4kCallerService wolfmax4KCallerService;

    @Test
    @SneakyThrows
    @Disabled
    public void test() {
        List<Index> result = Assertions.assertDoesNotThrow(() -> wolfmax4KCallerService.getAllFromAllPages());
        Assertions.assertNotNull(result);
    }

}
