package es.lavanda.downloader.bt4g.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class ScheduleService {

    private final MagnetService magnetService;
    private final Bt4gService bt4gService;


    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.DAYS)
    public void updateMagnets() {
        magnetService.updateMagnets();
        bt4gService.updateMagnetLink();
    }
}
