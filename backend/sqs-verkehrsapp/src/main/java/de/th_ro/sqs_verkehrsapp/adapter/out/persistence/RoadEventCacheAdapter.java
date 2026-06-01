package de.th_ro.sqs_verkehrsapp.adapter.out.persistence;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.CachedRoadEventEntity;
import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository.CachedRoadEventRepository;
import de.th_ro.sqs_verkehrsapp.application.port.out.RoadEventCachePort;
import de.th_ro.sqs_verkehrsapp.domain.logic.RiskScoreCalculator;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RoadEventCacheAdapter implements RoadEventCachePort {

    private static final String ALL_ROADS_CACHE_KEY = "ALL";
    private static final String AVAILABLE_ROADS_CACHE_KEY = "AVAILABLE_ROADS";

    private final CachedRoadEventRepository repository;
    private final RiskScoreCalculator riskScoreCalculator;

    public RoadEventCacheAdapter(CachedRoadEventRepository repository, RiskScoreCalculator riskScoreCalculator) {
        this.repository = repository;
        this.riskScoreCalculator = riskScoreCalculator;
    }

    @Override
    @Transactional
    public void save(String roadId, List<RoadEvent> events) {
        repository.deleteByRoadId(roadId);

        LocalDateTime cachedAt = LocalDateTime.now();

        List<CachedRoadEventEntity> entities = events.stream()
                .map(event -> new CachedRoadEventEntity(
                        roadId,
                        event.id(),
                        event.title(),
                        event.subtitle(),
                        event.type().name(),
                        event.coordinate().latitude(),
                        event.coordinate().longitude(),
                        cachedAt
                ))
                .toList();

        repository.saveAll(entities);
    }

    @Override
    public TrafficEventsResult findByRoadId(String roadId) {
        List<CachedRoadEventEntity> entities = repository.findByRoadId(roadId);

        if (entities.isEmpty()) {
            return new TrafficEventsResult(
                    List.of(),
                    false,
                    null,
                    0
            );
        }

        LocalDateTime cachedAt = entities.get(0).getCachedAt();

        List<RoadEvent> events = entities.stream()
                .map(entity -> {
                    RoadEventType type = RoadEventType.valueOf(entity.getType());

                    return new RoadEvent(
                            valueOrEmpty(entity.getEventId()),
                            valueOrEmpty(entity.getRoadId()),
                            valueOrEmpty(entity.getTitle()),
                            valueOrEmpty(entity.getSubtitle()),
                            "",
                            type,
                            new Coordinate(entity.getLatitude(), entity.getLongitude()),
                            riskScoreCalculator.calculateRiskLevel(type)
                    );
                })
                .toList();

        int riskScore = riskScoreCalculator.calculateRiskScore(events);

        return new TrafficEventsResult(
                events,
                false,
                cachedAt,
                riskScore
        );
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}
