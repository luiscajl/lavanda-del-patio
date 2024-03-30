package es.lavanda.filebot.executor.util;

public enum FilebotConstants {

    SCRIPT_AMC(" -script 'fn:amc' "),
    NO_XATTR(" -no-xattr "),
    NON_STRICT(" -non-strict "),
    ACTION_TEST(" --action test "),
    ACTION_MOVE(" --action move "),
    ACTION_COPY(" --action copy "),
    LANG_ES(" --lang es "),
    LANG_EN(" --lang en "),
    DATABASE(" --def movieDB=TheMovieDB seriesDB=TheMovieDB::TV animeDB=AniDB musicDB=ID3 "),
    // DATABASE(" --def movieDB=TheMovieDB seriesDB=TheTVDB animeDB=AniDB musicDB=ID3 "),
    ORDER_AIRDATE(" --order Airdate "),
    DEF(" --def "),
    LICENSE(" --license ");

    private final String text;

    FilebotConstants(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
