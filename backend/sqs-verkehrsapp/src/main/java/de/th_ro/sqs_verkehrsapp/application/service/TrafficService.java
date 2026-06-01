package de.th_ro.sqs_verkehrsapp.application.service;

import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficCacheRefreshUseCase;
import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.application.port.out.RoadEventCachePort;
import de.th_ro.sqs_verkehrsapp.domain.logic.RiskScoreCalculator;
import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrafficService implements TrafficQueryUseCase, TrafficCacheRefreshUseCase {

    private static final String ALL_ROADS_CACHE_KEY = "ALL";
    private final AutobahnApiPort autobahnApiPort;
    private final RoadEventCachePort cachePort;
    private final RiskScoreCalculator riskScoreCalculator;

    public TrafficService(AutobahnApiPort autobahnApiPort, RoadEventCachePort roadEventCachePort) {
        this.autobahnApiPort = autobahnApiPort;
        this.cachePort = roadEventCachePort;
        this.riskScoreCalculator = new RiskScoreCalculator();
    }

    @Override
    public TrafficEventsResult getTrafficEvents(String roadId) {
        TrafficEventsResult cachedResult = cachePort.findByRoadId(roadId);

        if (cachedResult.events() != null && !cachedResult.events().isEmpty()) {
            return withRiskScore(cachedResult);
        }

        TrafficEventsResult liveResult = autobahnApiPort.getTrafficEvents(roadId);

        return withRiskScore(liveResult);
    }

    @Override
    public TrafficEventsResult getAllTrafficEvents() {

        TrafficEventsResult cachedResult = cachePort.findByRoadId(ALL_ROADS_CACHE_KEY);

        if (cachedResult.events() != null && !cachedResult.events().isEmpty()) {
            return withRiskScore(cachedResult);
        }

        TrafficEventsResult liveResult = autobahnApiPort.getAllTrafficEvents();

        return withRiskScore(liveResult);
    }

    @Override
    public List<String> getAvailableRoadIds() {
        return autobahnApiPort.getAvailableRoadIds()
                .stream()
                .filter(roadId -> !"ALL".equals(roadId))
                .sorted()
                .toList();
    }

    @Override
    public void refreshCache() {
        autobahnApiPort.getAllTrafficEvents();
    }

    private TrafficEventsResult withRiskScore(TrafficEventsResult result) {
        int riskScore = riskScoreCalculator.calculateRiskScore(result.events());

        return new TrafficEventsResult(
                result.events(),
                result.live(),
                result.cachedAt(),
                riskScore
        );
    }
}
