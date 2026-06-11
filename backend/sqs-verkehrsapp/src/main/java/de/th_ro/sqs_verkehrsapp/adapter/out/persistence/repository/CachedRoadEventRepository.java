package de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.CachedRoadEventEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository für den Zugriff auf zwischengespeicherte Verkehrsereignisse.
 * <p>
 * Stellt CRUD-Operationen sowie Such- und Löschfunktionen für
 * cachebare Verkehrsereignisse bereit.
 */
public interface CachedRoadEventRepository extends JpaRepository<CachedRoadEventEntity, Long> {

    /**
     * Ermittelt alle zwischengespeicherten Verkehrsereignisse
     * einer bestimmten Autobahn.
     *
     * @param roadId Kennung der Autobahn
     * @return alle zugehörigen Verkehrsereignisse
     */
    List<CachedRoadEventEntity> findByRoadId(String roadId);

    /**
     * Entfernt alle zwischengespeicherten Verkehrsereignisse
     * einer bestimmten Autobahn.
     *
     * @param roadId Kennung der Autobahn
     */
    void deleteByRoadId(String roadId);
}
