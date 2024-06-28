package es.lavanda.tmdb.util;

import org.springframework.stereotype.Component;

import org.springframework.stereotype.Component;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.lavanda.lib.common.model.MediaIDTO;
import es.lavanda.lib.common.model.MediaODTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionODTO;
import es.lavanda.lib.common.model.tmdb.search.TMDBResultDTO;
import es.lavanda.lib.common.model.tmdb.search.TMDBSearchDTO;
import es.lavanda.tmdb.model.type.QueueType;
import es.lavanda.tmdb.service.ProducerService;
import es.lavanda.tmdb.service.impl.TMDBServiceShow;
import es.lavanda.tmdb.util.TmdbUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileUtils {

    private static final Pattern PATTERN_SHOW_4 = Pattern.compile("(.*)(\\(\\d{4}\\)).*");

    private static final Pattern PATTERN_SHOW_1 = Pattern.compile("(.*)(?:[. ]S\\d{1,2}.*)");
    private static final Pattern PATTERN_SHOW_3 = Pattern.compile("(.*).\\d{4}.S\\d{1,2}(.*)");
    private static final Pattern PATTERN_SHOW_2 = Pattern.compile("(.*)(?:[. ]Season[. ]\\d{1,2}.*)");

    public String getShortName(String pathOrFile) {
        Path path = Path.of(pathOrFile);
        String filename = path.getFileName().toString();
        log.info("Parent path or filename {}", filename);
        Matcher matcher1 = PATTERN_SHOW_1.matcher(filename);
        Matcher matcher2 = PATTERN_SHOW_2.matcher(filename);
        Matcher matcher3 = PATTERN_SHOW_3.matcher(filename);
        Matcher matcher4 = PATTERN_SHOW_4.matcher(filename);

        if (matcher4.matches()) {
            log.info("Regex {}", PATTERN_SHOW_4.pattern());
            return matcher4.group(1).replace(".", " ");
        } else if (matcher3.matches()) {
            log.info("Regex {}", PATTERN_SHOW_3.pattern());
            return matcher3.group(1).replace(".", " ");
        } else if (matcher1.matches()) {
            log.info("Regex {}", PATTERN_SHOW_1.pattern());
            return matcher1.group(1).replace(".", " ");
        } else if (matcher2.matches()) {
            log.info("Regex {}", PATTERN_SHOW_2.pattern());
            return matcher2.group(1).replace(".", " ");
        } else if (filename.contains(" - ") && filename.contains("]") && filename.contains("[")) {
            log.info("Regex  -");
            return filename.split("-")[0];
        } else if (filename.contains("[") && filename.contains("]")) {
            log.info("Regex  [");
            return filename.split("\\[")[0];
        } else if (filename.contains("(") && filename.contains(")")) {
            log.info("Regex  (");
            return filename.split("\\(")[0];
        } else if (filename.contains("[") && filename.contains("]")) {
            log.info("Regex [");
            return filename.split("\\[")[0];
        } else if (filename.contains("(") && filename.contains(")")) {
            log.info("Regex (");
            return filename.split("\\(")[0];
        } else if (filename.endsWith(".mkv")) {
            log.info("Regex .mkv");
            return filename.split("\\.")[0];
        } else {
            log.info("Without regex");
            return filename;
        }

    }

}
