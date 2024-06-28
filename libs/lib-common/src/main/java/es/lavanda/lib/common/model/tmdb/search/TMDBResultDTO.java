package es.lavanda.lib.common.model.tmdb.search;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class TMDBResultDTO implements Serializable {

    private int id;

    @JsonProperty("poster_path")
    private String posterPath;

    private boolean adult;

    private String overview;

    private String title;

    @JsonProperty("original_title")
    private String originalTitle;

    private String name;

    @JsonProperty("original_name")
    private String originalName;

    @JsonProperty("release_date")
    private String releaseDate;

    @JsonProperty("first_air_date")
    private String firstAirDate;

    @JsonProperty("genre_ids")
    private List<Integer> genreIds;

    @JsonProperty("media_type")
    private MediaTypeEnum mediaType;

    @JsonProperty("original_language")
    private String originalLanguage;

    @JsonProperty("backdrop_path")
    private String backdropPath;

    private int popularity;

    @JsonProperty("vote_count")
    private int voteCount;

    private boolean video;

    @JsonProperty("vote_average")
    private float voteAverage;

    public enum MediaTypeEnum {
        MOVIE("movie"), TV("tv"), PERSON("person");

        private String value;

        MediaTypeEnum(String value) {
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
        public static MediaTypeEnum fromValue(String value) {
            for (MediaTypeEnum b : MediaTypeEnum.values()) {
                if (b.value.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

    }
}
