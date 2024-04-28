package es.lavanda.filebot.service.impl;

import es.lavanda.filebot.exception.FilebotException;
import es.lavanda.filebot.model.Filebot;
import es.lavanda.filebot.model.FilebotFile;
import es.lavanda.filebot.repository.FilebotFileRepository;
import es.lavanda.filebot.repository.FilebotRepository;
import es.lavanda.filebot.service.FilebotParserService;
import es.lavanda.filebot.util.FilebotParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Slf4j
public class FilebotParserServiceImpl implements FilebotParserService {

    @Autowired
    private FilebotRepository filebotRepository;

    @Autowired
    private FilebotFileRepository filebotFileRepository;

    @Autowired
    private FilebotParser filebotParser;

    @Value("${filebot.path.data}")
    private String FILEBOT_PATH;


    private String getHtmlData(String filePath) {
        try {
            log.info("getHtmlData of {}", filePath);
            log.info("Path of {}", Path.of(filePath));
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            log.error("Can not access to path {}", FILEBOT_PATH, e);
            throw new FilebotException("Can not access to path", e);
        }
    }

    private List<FilebotFile> getAllFilesFounded(String path) {
        try (Stream<Path> walk = Files.walk(Paths.get(path))) {

            return walk.filter(Files::isRegularFile).map(filePath -> new FilebotFile(filePath.toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Can not access to path {}", FILEBOT_PATH, e);
            throw new FilebotException("Can not access to path", e);
        }
    }


    public void run() {
        log.info("Start schedule parse new files");
        List<FilebotFile> newFiles = getAllFilesFounded(FILEBOT_PATH);
        List<FilebotFile> oldFiles = filebotFileRepository.findAll();
        newFiles.removeAll(oldFiles);
        newFiles.forEach(file -> {
            log.info("Parsing new file {}", file.getFilePath());
            List<Filebot> filebots = filebotParser.parseHtml(getHtmlData(file.getFilePath()));
            filebotRepository.saveAll(filebots);
            filebotFileRepository.save(file);
        });
        log.info("Finish schedule parse new files");
    }
}

