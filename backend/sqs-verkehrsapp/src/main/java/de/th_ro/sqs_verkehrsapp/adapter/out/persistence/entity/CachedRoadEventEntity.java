package de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA-Entität zur Persistierung von Verkehrsereignissen im lokalen Cache.
 * <p>
 * Die Entität speichert die von der Autobahn-API abgerufenen Ereignisse
 * zusammen mit dem Zeitpunkt der Zwischenspeicherung, um bei Ausfällen
 * der API auf bereits geladene Daten zurückgreifen zu können.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class CachedRoadEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roadId;
    private String eventId;
    private String title;
    private String subtitle;
    private String type;
    private double latitude;
    private double longitude;
    private LocalDateTime cachedAt;

    /**
     * Erstellt eine neue Cache-Entität für ein Verkehrsereignis.
     */
    public CachedRoadEventEntity(
            String roadId,
            String eventId,
            String title,
            String subtitle,
            String type,
            double latitude,
            double longitude,
            LocalDateTime cachedAt
    ) {
        this.roadId = roadId;
        this.eventId = eventId;
        this.title = title;
        this.subtitle = subtitle;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
        this.cachedAt = cachedAt;
    }
}
