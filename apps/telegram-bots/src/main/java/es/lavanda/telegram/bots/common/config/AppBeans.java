package es.lavanda.telegram.bots.common.config;

import es.lavanda.telegram.bots.classify.config.ClassifyConfig;
import es.lavanda.telegram.bots.filebot.config.FilebotConfig;
import es.lavanda.telegram.bots.filebot.handler.FilebotHandler;
import es.lavanda.telegram.bots.filebot.service.FilebotService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ApiResponse;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@EnableMongoAuditing
@Slf4j
@EnableScheduling
@RegisterReflectionForBinding({ApiResponse.class, DefaultBotSession.class, BotApiMethodMessage.class})
public class AppBeans {

    // @Autowired
    // private ClassifyService classifyService;

    @Autowired
    private FilebotService filebotService;

    @Autowired
    private FilebotConfig filebotConfig;

    @Autowired
    private ClassifyConfig classifyConfig;

    private BotSession botSession;

    private BotSession botSessionFilebotExecutor;

    @PostConstruct
    public void start() throws TelegramApiException {
        log.info("Instantiate Telegram Bots API...");
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        // addClassifyHandler(botsApi);
        addFilebotHandler(botsApi);
    }

    // @PreDestroy
    // public void preDestroy() {
    // log.info("PreDestroy BOT");
    // botSession.stop();
    // botSessionFilebotExecutor.stop();
    // log.info("Destroyed");
    // }

    private void addFilebotHandler(TelegramBotsApi botsApi) throws TelegramApiException {
        FilebotHandler filebotHandler = new FilebotHandler(
                filebotService, filebotConfig);
        filebotService.setFilebotHandler(filebotHandler);
        botSession = botsApi.registerBot(filebotHandler);
    }

    // private void addClassifyHandler(TelegramBotsApi botsApi) throws
    // TelegramApiException {
    // ClassifyHandler classifyHandler = new ClassifyHandler(
    // classifyService, classifyConfig);
    // classifyService.setClassifyHandler(classifyHandler);
    // botSession = botsApi.registerBot(
    // classifyHandler);
    // }

}
