package es.lavanda.tmdb.model.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QueueType {
    FEED_FILMS_RESOLUTION("agent-tmdb-feed-films-resolution"),
    FEED_SHOWS_RESOLUTION("agent-tmdb-feed-shows-resolution"),
    FEED_FILMS("agent-tmdb-feed-films"),
    FEED_SHOWS("agent-tmdb-feed-shows"),
    TELEGRAM_QUERY_TMDB("telegram-query-tmdb"),
    TELEGRAM_QUERY_TMDB_RESOLUTION("telegram-query-tmdb-resolution");

    private String value;

    QueueType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static QueueType fromValue(String value) {
        for (QueueType b : QueueType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

}
