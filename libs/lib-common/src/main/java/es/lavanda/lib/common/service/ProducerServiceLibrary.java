package es.lavanda.lib.common.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerExecutionChain;

import es.lavanda.lib.common.exception.HandlerException;
import es.lavanda.lib.common.model.FilebotExecutionIDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProducerServiceLibrary {

    private final RabbitTemplate rabbitTemplate;

    public <T> void sendMessageToQueue(String queueName, T object) {
        try {
            log.info("Sending message to queue {}", queueName);
            rabbitTemplate.convertAndSend(queueName, object);
            log.info("Sended message to queue");
        } catch (Exception e) {
            log.error("Failed send message to queue {}", queueName, e);
            throw new HandlerException("Failed send message to queue", e);
        }
    }
}
