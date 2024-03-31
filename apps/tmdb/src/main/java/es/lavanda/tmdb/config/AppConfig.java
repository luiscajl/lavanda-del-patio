package es.lavanda.tmdb.config;

import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionODTO;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

@Configuration
public class AppConfig implements RuntimeHintsRegistrar {

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

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.serialization().registerType(TelegramFilebotExecutionODTO.class);
        hints.serialization().registerType(TelegramFilebotExecutionIDTO.class);
    }

}
