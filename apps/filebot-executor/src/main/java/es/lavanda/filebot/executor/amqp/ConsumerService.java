package es.lavanda.filebot.executor.amqp;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import es.lavanda.filebot.executor.service.FilebotExecutionService;
import es.lavanda.lib.common.model.FilebotExecutionODTO;
import es.lavanda.lib.common.model.FilebotExecutionTestODTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {

    private static final String FILEBOT_TELEGRAM_RESOLUTION = "filebot-telegram-resolution";

    private static final String FILEBOT_TELEGRAM_TEST_RESOLUTION = "filebot-telegram-test-resolution";

    private final FilebotExecutionService filebotExecutorService;

    @RabbitListener(queues = FILEBOT_TELEGRAM_RESOLUTION)
    public void consumeMessageFilebotTelegramResolution(FilebotExecutionODTO filebotExecutionODTO) {
        log.info("Reading message of the queue {}: {}", FILEBOT_TELEGRAM_RESOLUTION, filebotExecutionODTO);
        filebotExecutorService.resolutionTelegramBot(filebotExecutionODTO);
        log.info("Work message finished");
    }

    @RabbitListener(queues = FILEBOT_TELEGRAM_TEST_RESOLUTION)
    public void consumeMessageFilebotTelegramTestResolution(FilebotExecutionTestODTO filebotExecutionTestODTO) {
        log.info("Reading message of the queue {}: {}", FILEBOT_TELEGRAM_TEST_RESOLUTION, filebotExecutionTestODTO);
        filebotExecutorService.resolutionTelegramBotTest(filebotExecutionTestODTO);
        log.info("Work message finished");
    }
}
