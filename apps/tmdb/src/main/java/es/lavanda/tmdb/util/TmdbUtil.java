package es.lavanda.tmdb.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import es.lavanda.lib.common.model.tmdb.search.TMDBSearchDTO;
import lombok.extern.slf4j.Slf4j;

// @Component
@Slf4j
public class TmdbUtil {

    private static final String TMDB_API_TV_INIT = "https://api.themoviedb.org/3/tv/";
    private static final String TMDB_API_TV_SEARCH = "https://api.themoviedb.org/3/search/tv";
    private static final String TMDB_API_MULTI_SEARCH = "https://api.themoviedb.org/3/search/multi";

    // TODO IDIOMAS
    // private static final String TMDB_API_LANGUAGE_ES = "&language=es-ES";
    /* INFO IMAGES: https://goo.gl/9UqTKn */
    private static final String URI_PHOTOS_FRONT_TMDB = "https://image.tmdb.org/t/p/w300_and_h450_bestv2";
    private static final String URI_PHOTOS_ORIGINAL_TMDB = "https://image.tmdb.org/t/p/original";
    private static final String URI_PHOTOS_W500_TMDB = "https://image.tmdb.org/t/p/w500";
    private static final String URI_PHOTOS_W780_TMDB = "https://image.tmdb.org/t/p/w780";


    private static final String URI_PHOTOS_STILL_TMDB = "https://image.tmdb.org/t/p/w300";

    private static final String URI_SEARCH_MOVIE_PAGE = "&page=1&year=";

    private static final String TMDB_API_MOVIE_INIT = "https://api.themoviedb.org/3/movie/";
    private static final String TMDB_API_MOVIE_SEARCH = "https://api.themoviedb.org/3/search/movie";

    @Value("${tmdb.apikey}")
    private static String TMDB_API_KEY;

    private static final String TMDB_CREDITS = "/credits";

    private static final String TMDB_API_LANGUAGE_ES = "es-ES";

    public static TMDBSearchDTO multiSearch(String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(TMDB_API_MULTI_SEARCH)
                .queryParam("api_key", TMDB_API_KEY).queryParam("language", TMDB_API_LANGUAGE_ES)
                .queryParam("query", query).queryParam("page", 1).queryParam("include_adult", false);

        HttpEntity<?> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        log.info("Final uri {}", builder.toUriString());
        ResponseEntity<TMDBSearchDTO> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET,
                entity, TMDBSearchDTO.class);
        return response.getBody();
    }

    public static String getOriginalImage(String path) {
        return URI_PHOTOS_ORIGINAL_TMDB + path;
    }

    public static String getW500Image(String path) {
        return URI_PHOTOS_W500_TMDB + path;
    }

    public static String getW780Image(String path) {
        return URI_PHOTOS_W780_TMDB + path;
    }

}
