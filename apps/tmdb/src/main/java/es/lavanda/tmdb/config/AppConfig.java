package es.lavanda.tmdb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;

@Configuration
public class AppConfig {

    @Value("${tmdb.apikey}")
    private String API_KEY_TMDB;

    @Value("${tmdb.language}")
    private String LANGUAGE_TMDB = "es-ES";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.query("api_key", API_KEY_TMDB);
            requestTemplate.query("language", LANGUAGE_TMDB);
        };
    }
}
