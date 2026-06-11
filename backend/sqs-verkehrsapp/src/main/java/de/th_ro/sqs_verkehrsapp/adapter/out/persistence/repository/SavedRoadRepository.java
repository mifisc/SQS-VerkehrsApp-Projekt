package de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.SavedRoadEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository für den Zugriff auf vom Benutzer gespeicherte Autobahnen.
 * <p>
 * Stellt CRUD-Operationen sowie Such- und Verwaltungsfunktionen
 * für Favoriten bereit.
 */
public interface SavedRoadRepository extends JpaRepository<SavedRoadEntity, UUID> {

    /**
     * Liefert alle von einem Benutzer gespeicherten Autobahnen.
     *
     * @param userId Kennung des Benutzers
     * @return Liste der gespeicherten Autobahnen
     */
    List<SavedRoadEntity> findByUserId(UUID userId);

    /**
     * Prüft, ob ein Benutzer eine bestimmte Autobahn bereits gespeichert hat.
     *
     * @param userId Kennung des Benutzers
     * @param roadId Kennung der Autobahn
     * @return {@code true}, wenn die Autobahn bereits gespeichert ist,
     *         andernfalls {@code false}
     */
    boolean existsByUserIdAndRoadId(UUID userId, String roadId);

    /**
     * Entfernt eine gespeicherte Autobahn eines Benutzers.
     *
     * @param userId Kennung des Benutzers
     * @param roadId Kennung der Autobahn
     */
    void deleteByUserIdAndRoadId(UUID userId, String roadId);
}
