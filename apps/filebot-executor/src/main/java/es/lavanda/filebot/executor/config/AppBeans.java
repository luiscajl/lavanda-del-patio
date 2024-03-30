package es.lavanda.filebot.executor.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
@EnableAutoConfiguration
public class AppBeans {

    @Bean
    public ExecutorService executorServiceBean() {
        return Executors.newFixedThreadPool(10);
    }
}