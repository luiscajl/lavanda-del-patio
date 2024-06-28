package es.lavanda.filebot.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.lavanda.lib.common.model.FilebotExecutionIDTO;
import es.lavanda.lib.common.model.FilebotExecutionTestIDTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionODTO;
import io.kubernetes.client.openapi.models.V1Job;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
@EnableAutoConfiguration
@RegisterReflectionForBinding({FilebotExecutionIDTO.class, FilebotExecutionTestIDTO.class})
public class AppBeans {

    @Bean
    public ExecutorService executorServiceBean() {
        return Executors.newFixedThreadPool(10);
    }
}