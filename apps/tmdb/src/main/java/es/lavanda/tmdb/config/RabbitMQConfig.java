package es.lavanda.tmdb.config;

import es.lavanda.lib.common.model.TelegramFilebotExecutionODTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_MESSAGES = "agent-tmdb-feed-films";
    public static final String QUEUE_MESSAGES_DLQ = "agent-tmdb-feed-films-dlq";

    public static final String QUEUE_MESSAGES_SHOWS = "agent-tmdb-feed-shows";
    public static final String QUEUE_MESSAGES_SHOWS_DLQ = "agent-tmdb-feed-shows-dlq";

    public static final String QUEUE_TELEGRAM_QUERY_TMDB = "telegram-query-tmdb";
    public static final String QUEUE_TELEGRAM_QUERY_TMDB_DLQ = "telegram-query-tmdb-dlq";

    public static final String EXCHANGE_MESSAGES = "lavandadelpatio-exchange";

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("es.lavanda.lib.common.model.TelegramFilebotExecutionIDTO",
                TelegramFilebotExecutionIDTO.class);
        idClassMapping.put("es.lavanda.lib.common.model.TelegramFilebotExecutionODTO",
                TelegramFilebotExecutionODTO.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter jsonConverter = new Jackson2JsonMessageConverter();
        jsonConverter.setClassMapper(classMapper());
        return jsonConverter;
    }

    @Bean("rabbitTemplateOverrided")
    @Primary
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final var rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    DirectExchange messagesExchange() {
        return new DirectExchange(EXCHANGE_MESSAGES);
    }

    @Bean
    Binding bindingMessages() {
        return BindingBuilder.bind(messagesQueue()).to(messagesExchange()).with(QUEUE_MESSAGES);
    }

    @Bean
    Queue messagesQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES).withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_MESSAGES_DLQ).build();
    }

    @Bean
    Queue deadLetterQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES_DLQ).build();
    }

    @Bean
    Binding bindingTelegramShows() {
        return BindingBuilder.bind(messagesQueue()).to(messagesExchange()).with(QUEUE_TELEGRAM_QUERY_TMDB);
    }

    @Bean
    Queue messagesTelegramQueue() {
        return QueueBuilder.durable(QUEUE_TELEGRAM_QUERY_TMDB).withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_TELEGRAM_QUERY_TMDB_DLQ).build();
    }

    @Bean
    Queue deadLetterTelegramQueue() {
        return QueueBuilder.durable(QUEUE_TELEGRAM_QUERY_TMDB_DLQ).build();
    }

    @Bean
    Binding bindingMessagesShows() {
        return BindingBuilder.bind(messagesQueue()).to(messagesExchange()).with(QUEUE_MESSAGES_SHOWS);
    }

    @Bean
    Queue messagesShowsQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES_SHOWS).withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_MESSAGES_SHOWS_DLQ).build();
    }

    @Bean
    Queue deadLetterShowsQueue() {
        return QueueBuilder.durable(QUEUE_MESSAGES_SHOWS_DLQ).build();
    }
}
