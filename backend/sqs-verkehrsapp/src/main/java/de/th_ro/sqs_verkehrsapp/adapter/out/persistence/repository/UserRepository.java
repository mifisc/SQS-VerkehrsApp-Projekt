package de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository für den Zugriff auf Anwendungsbenutzer.
 * <p>
 * Stellt CRUD-Operationen sowie benutzerspezifische Suchfunktionen
 * für die Authentifizierung und Benutzerverwaltung bereit.
 */
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    /**
     * Sucht einen Benutzer anhand seines Benutzernamens.
     *
     * @param username Benutzername
     * @return den gefundenen Benutzer oder ein leeres {@link Optional},
     *         falls kein Benutzer existiert
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Prüft, ob ein Benutzer mit dem angegebenen Benutzernamen existiert.
     *
     * @param username Benutzername
     * @return {@code true}, wenn ein Benutzer existiert,
     *         andernfalls {@code false}
     */
    boolean existsByUsername(String username);
}
