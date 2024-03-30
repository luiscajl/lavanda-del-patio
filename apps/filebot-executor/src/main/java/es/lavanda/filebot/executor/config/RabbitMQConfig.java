package es.lavanda.filebot.executor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import es.lavanda.lib.common.model.FilebotExecutionIDTO;
import es.lavanda.lib.common.model.FilebotExecutionODTO;
import es.lavanda.lib.common.model.FilebotExecutionTestODTO;
import es.lavanda.lib.common.model.TelegramFilebotExecutionODTO;

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

    private static final String QUEUE_MESSAGES = "filebot-telegram-resolution";

    private static final String QUEUE_MESSAGES_DLQ = "filebot-telegram-resolution-dlq";

    private static final String QUEUE_MESSAGES_TEST = "filebot-telegram-test-resolution";

    private static final String QUEUE_MESSAGES_TEST_DLQ = "filebot-telegram-test-resolution-dlq";

    private static final String EXCHANGE_MESSAGES = "lavandadelpatio-exchange";

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("es.lavanda.lib.common.model.TelegramFilebotExecutionODTO",
                TelegramFilebotExecutionODTO.class);
        idClassMapping.put("es.lavanda.lib.common.model.FilebotExecutionIDTO",
                FilebotExecutionIDTO.class);
        idClassMapping.put("es.lavanda.lib.common.model.FilebotExecutionODTO",
                FilebotExecutionODTO.class);
        idClassMapping.put("es.lavanda.lib.common.model.FilebotExecutionTestODTO",
                FilebotExecutionTestODTO.class);
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
    Binding bindingMessagesTest() {
        return BindingBuilder.bind(messagesQueue()).to(messagesExchange()).with(QUEUE_MESSAGES_TEST);
    }

    @Bean
    Queue messagesQueueTest() {
        return QueueBuilder.durable(QUEUE_MESSAGES_TEST).withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", QUEUE_MESSAGES_TEST_DLQ).build();
    }

    @Bean
    Queue deadLetterQueueTest() {
        return QueueBuilder.durable(QUEUE_MESSAGES_TEST_DLQ).build();
    }
}
