package de.th_ro.sqs_verkehrsapp.adapter.out.autobahn;

import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.application.port.out.RoadEventCachePort;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class ResilientAutobahnApiAdapter implements AutobahnApiPort {

    private final AutobahnApiClient autobahnApiClient;
    private final RoadEventCachePort cachePort;

    public ResilientAutobahnApiAdapter(
            AutobahnApiClient autobahnApiClient,
            RoadEventCachePort cachePort
    ) {
        this.autobahnApiClient = autobahnApiClient;
        this.cachePort = cachePort;
    }

    @Override
    @Retry(name = "autobahnApi", fallbackMethod = "getTrafficEventsFallback")
    @CircuitBreaker(name = "autobahnApi", fallbackMethod = "getTrafficEventsFallback")
    public List<RoadEvent> getTrafficEvents(String roadId) {
        List<RoadEvent> events = autobahnApiClient.fetchTrafficEvents(roadId);

        cachePort.save(roadId, events);

        return events;
    }

    public List<RoadEvent> getTrafficEventsFallback(String roadId, Throwable throwable) {
        return cachePort.findByRoadId(roadId);
    }
}
