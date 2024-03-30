package es.lavanda.filebot.executor.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class ScheduleService {


    private final FilebotExecutionService filebotExecutorService;

    // @Scheduled(cron = "0 0 * * * *")
    @Scheduled(fixedDelay = 60000)
    public void executeSchedule() {
        filebotExecutorService.forceExecute();
    }

    // @Scheduled(cron = "0 0/15 * * * *")
    @Scheduled(fixedDelay = 60000)
    public void checkPossiblesNewFilebotExecution() {
        filebotExecutorService.checkPossiblesNewFilebotExecution();
    }

}
