package es.lavanda.filebot.service;

import es.lavanda.filebot.util.FilebotParser;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class FilebotParserTest {
    @InjectMocks
    private FilebotParser filebotParser;

    @Test
    @SneakyThrows
    public void test() {
        Path fileName = Path.of("src/main/resources/filebot/1.html");
        String file = Files.readString(fileName);
        Assertions.assertDoesNotThrow(() -> filebotParser.parseHtml(file));
    }
}
