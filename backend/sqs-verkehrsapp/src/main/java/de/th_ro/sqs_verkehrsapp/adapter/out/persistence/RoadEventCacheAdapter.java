package de.th_ro.sqs_verkehrsapp.adapter.out.persistence;

import de.th_ro.sqs_verkehrsapp.application.port.out.RoadEventCachePort;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RoadEventCacheAdapter implements RoadEventCachePort {

    private final CachedRoadEventRepository repository;

    public RoadEventCacheAdapter(CachedRoadEventRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void save(String roadId, List<RoadEvent> events) {
        repository.deleteByRoadId(roadId);

        List<CachedRoadEventEntity> entities = events.stream()
                .map(event -> new CachedRoadEventEntity(
                        event.roadId(),
                        event.id(),
                        event.title(),
                        event.subtitle(),
                        event.type().name(),
                        event.coordinate().latitude(),
                        event.coordinate().longitude(),
                        LocalDateTime.now()
                ))
                .toList();

        repository.saveAll(entities);
    }

    @Override
    public List<RoadEvent> findByRoadId(String roadId) {
        return repository.findByRoadId(roadId)
                .stream()
                .map(entity -> new RoadEvent(
                        entity.getEventId(),
                        entity.getRoadId(),
                        entity.getTitle(),
                        entity.getSubtitle(),
                        "",
                        RoadEventType.valueOf(entity.getType()),
                        new Coordinate(entity.getLatitude(), entity.getLongitude()),
                        null
                ))
                .toList();
    }
}
