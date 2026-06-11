package de.th_ro.sqs_verkehrsapp.adapter.out.persistence;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.CachedRoadEventEntity;
import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository.CachedRoadEventRepository;
import de.th_ro.sqs_verkehrsapp.application.port.out.RoadEventCachePort;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Persistence-Adapter für den Cache von Verkehrsereignissen.
 * <p>
 * Implementiert {@link RoadEventCachePort} und speichert bzw. lädt
 * Verkehrsereignisse aus der Datenbank. Die Domain-Objekte werden dabei
 * in Persistenz-Entitäten umgewandelt und umgekehrt.
 */
@Component
public class RoadEventCacheAdapter implements RoadEventCachePort {

    private final CachedRoadEventRepository repository;

    /**
     * Erstellt einen neuen Adapter für den Zugriff auf den Ereignis-Cache.
     *
     * @param repository Repository für zwischengespeicherte Verkehrsereignisse
     */
    public RoadEventCacheAdapter(CachedRoadEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Speichert die Verkehrsereignisse einer Autobahn im Cache.
     * Bereits vorhandene Cache-Einträge für die Autobahn werden zuvor entfernt.
     *
     * @param roadId Kennung der Autobahn
     * @param events zu speichernde Verkehrsereignisse
     */
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

    /**
     * Lädt die zwischengespeicherten Verkehrsereignisse einer Autobahn.
     *
     * @param roadId Kennung der Autobahn
     * @return das Ergebnisobjekt mit den geladenen Ereignissen und
     *         den zugehörigen Cache-Informationen
     */
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

        return new TrafficEventsResult(
                events,
                false,
                cachedAt,
                0
        );
    }
}
