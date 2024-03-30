package es.lavanda.tmdb.service;

import es.lavanda.tmdb.exception.TMDBException;
import es.lavanda.tmdb.model.type.QueueType;

import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProducerService {

    @Autowired
    @Qualifier("rabbitTemplateOverrided")
    private final RabbitTemplate rabbitTemplate;

    public void sendMessage(Object message, QueueType type) throws TMDBException {
        try {
            log.debug("Sending message to queue {}", type.getValue());
            rabbitTemplate.convertAndSend(type.getValue(), message, new MessagePostProcessor() {
                @Override
                public org.springframework.amqp.core.Message postProcessMessage(
                        org.springframework.amqp.core.Message message) {
                    message.getMessageProperties().setHeader("x-delay", 1000);
                    return message;
                }
            });
            log.debug("Sended message to queue");
        } catch (Exception e) {
            log.error("Failed send message to queue {}", type.getValue(), e);
            throw new TMDBException("Failed send message to queue", e);
        }
    }

}
