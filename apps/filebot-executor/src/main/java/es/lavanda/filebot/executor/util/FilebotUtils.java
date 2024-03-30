package es.lavanda.filebot.executor.util;

import java.nio.file.Path;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.lavanda.lib.common.model.filebot.FilebotAction;
import es.lavanda.lib.common.model.filebot.FilebotCategory;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FilebotUtils {
    private final String FILEBOT = "filebot";

    @Value("${filebot.path.input}")
    private String FILEBOT_PATH_INPUT;

    @Value("${filebot.path.output}")
    private String FILEBOT_PATH_OUTPUT;

    @Value("${filebot.path.data}")
    private String FILEBOT_PATH_DATA;

    @Value("${filebot.test.enabled}")
    private boolean FILEBOT_TEST_ENABLED;

    public String getRegisterCommand() {
        return FILEBOT + FilebotConstants.LICENSE.toString() +
                getFilebotPathData().trim() + "/license.psm";
    }

    public String getFilebotCommand(Path folderPath, String query, FilebotCategory category, boolean forceStrict,
            boolean english,
            FilebotAction action, boolean testPhase) {
        log.info("FOLDER PATH {}", folderPath);
        String queryFilled = "";
        String nonStrict = "";
        String utLabel = "";
        String lang = FilebotConstants.LANG_EN.toString();
        String showsFormat = getShowsFormat();
        if (Objects.nonNull(query)) {
            queryFilled = " --q \"" + query + "\" ";
        }
        if (forceStrict) {
            nonStrict = FilebotConstants.NON_STRICT.toString();
        }
        if (Objects.nonNull(category)) {
            switch (category) {
                case TV, TV_EN:
                    utLabel = " --def \"ut_label=SERIES\" ";
                    break;
                case FILM:
                    utLabel = " --def \"ut_label=MOVIE\" ";
                    break;
                default:
                    break;
            }
        }
        if (english) {
            showsFormat = getShowsEnFormat();
        }
        return FILEBOT + FilebotConstants.SCRIPT_AMC.toString() +
                getOutputFormat() +
                getAction(action, testPhase) +
                getDatabase() +
                lang +
                FilebotConstants.ORDER_AIRDATE.toString() +
                FilebotConstants.NO_XATTR.toString() +
                "\"" + folderPath + "\"" +
                FilebotConstants.DEF.toString() +
                getMoviesFormat() +
                showsFormat +
                getStoreReport() +
                getUnsortedFormat() +
                // getExcludeList() +
                queryFilled + nonStrict + utLabel;
    }

    private String getAction(FilebotAction action, boolean testPhase) {
        if (Boolean.TRUE.equals(FILEBOT_TEST_ENABLED) || Boolean.TRUE.equals(testPhase)) {
            return FilebotConstants.ACTION_TEST.toString();
        }
        switch (action) {
            case COPY:
                return FilebotConstants.ACTION_COPY.toString();
            case MOVE:
                return FilebotConstants.ACTION_MOVE.toString();
            default:
                return FilebotConstants.ACTION_COPY.toString();
        }
    }

    public String getFilebotPathInput() {
        return FILEBOT_PATH_INPUT;
    }

    public String getFilebotPathOutput() {
        return FILEBOT_PATH_OUTPUT;
    }

    public String getFilebotPathData() {
        return FILEBOT_PATH_DATA;
    }

    private enum Constants {
        MOVIES_FORMAT(
                " 'movieFormat=FILEBOT_PATH_OUTPUT/Peliculas/{n} ({y})/{n} ({y})_{audioLanguages}_{vf}_{bitrate}' "),
        SHOWS_FORMAT(
                " 'seriesFormat=FILEBOT_PATH_OUTPUT/Series/{n}/ Season {s}/{n} s{s.pad(2)}e{e.pad(2)}_{audioLanguages}_{vf}_{bitrate}' "),
        SHOWS_EN_FORMAT(
                " 'seriesFormat=FILEBOT_PATH_OUTPUT/Series-EN/{n}/ Season {s}/{n} s{s.pad(2)}e{e.pad(2)}_{audioLanguages}_{vf}_{bitrate}' "),
        OUTPUT_FORMAT(" --output \"FILEBOT_PATH_OUTPUT\" "),
        UNSORTED_FORMAT(" 'unsortedFormat=FILEBOT_PATH_INPUT/Unsorted/{fn}.{ext}' "),
        STORE_REPORT(" --def 'storeReport=FILEBOT_PATH_DATA/.reports' "),
        EXCLUDE_LIST(" --def 'excludeList=FILEBOT_PATH_DATA/.excludes' ");

        private final String text;

        Constants(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }

    private String getDatabase() {
        return FilebotConstants.DATABASE.toString();
    }

    private String getMoviesFormat() {
        return Constants.MOVIES_FORMAT.toString().replace("FILEBOT_PATH_OUTPUT", FILEBOT_PATH_OUTPUT);
    }

    private String getShowsFormat() {
        return Constants.SHOWS_FORMAT.toString().replace("FILEBOT_PATH_OUTPUT", FILEBOT_PATH_OUTPUT);
    }

    private String getShowsEnFormat() {
        return Constants.SHOWS_EN_FORMAT.toString().replace("FILEBOT_PATH_OUTPUT", FILEBOT_PATH_OUTPUT);
    }

    private String getUnsortedFormat() {
        return Constants.UNSORTED_FORMAT.toString().replace("FILEBOT_PATH_INPUT", FILEBOT_PATH_OUTPUT);
    }

    private String getStoreReport() {
        return Constants.STORE_REPORT.toString().replace("FILEBOT_PATH_DATA", FILEBOT_PATH_DATA);
    }

    private String getOutputFormat() {
        return Constants.OUTPUT_FORMAT.toString().replace("FILEBOT_PATH_OUTPUT", FILEBOT_PATH_OUTPUT);
    }

}
