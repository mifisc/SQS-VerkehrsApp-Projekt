package de.th_ro.sqs_verkehrsapp.adapter.in.scheduler;

import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficCacheRefreshUseCase;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TrafficCacheScheduler {

    private final TrafficCacheRefreshUseCase refreshUseCase;

    public TrafficCacheScheduler(
            TrafficCacheRefreshUseCase refreshUseCase
    ) {
        this.refreshUseCase = refreshUseCase;
    }

    @Scheduled(fixedRate = 300000)
    public void refreshTrafficCache() {
        refreshUseCase.refreshCache();
    }
}