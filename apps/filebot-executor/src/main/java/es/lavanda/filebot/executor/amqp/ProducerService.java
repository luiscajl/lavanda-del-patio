package es.lavanda.filebot.executor.amqp;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import es.lavanda.filebot.executor.exception.FilebotExecutorException;
import es.lavanda.lib.common.model.FilebotExecutionIDTO;
import es.lavanda.lib.common.model.FilebotExecutionTestIDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProducerService {

    @Autowired
    @Qualifier("rabbitTemplateOverrided")
    private final RabbitTemplate rabbitTemplate;

    private static final String FILEBOT_TELEGRAM = "filebot-telegram";

    private static final String FILEBOT_TELEGRAM_TEST = "filebot-telegram-test";

    public void sendFilebotExecutionToTelegram(FilebotExecutionIDTO filebot) {
        try {
            log.info("Sending message to queue {}", FILEBOT_TELEGRAM);
            rabbitTemplate.convertAndSend(FILEBOT_TELEGRAM, filebot);
            log.info("Sended message to queue");
        } catch (Exception e) {
            log.error("Failed send message to queue {}", FILEBOT_TELEGRAM, e);
            throw new FilebotExecutorException("Failed send message to queue", e);
        }
    }

    public void sendFilebotToConfirmTest(FilebotExecutionTestIDTO filebot) {
        try {
            log.info("Sending message to queue {}", FILEBOT_TELEGRAM_TEST);
            rabbitTemplate.convertAndSend(FILEBOT_TELEGRAM_TEST, filebot);
            log.info("Sended message to queue");
        } catch (Exception e) {
            log.error("Failed send message to queue {}", FILEBOT_TELEGRAM_TEST, e);
            throw new FilebotExecutorException("Failed send message to queue", e);
        }
    }
}
