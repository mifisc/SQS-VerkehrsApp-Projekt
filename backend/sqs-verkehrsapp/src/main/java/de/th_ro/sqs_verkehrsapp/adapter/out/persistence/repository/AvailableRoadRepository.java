package de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.AvailableRoadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository für den Zugriff auf gespeicherte verfügbare Autobahnen.
 * <p>
 * Stellt CRUD-Operationen für {@link AvailableRoadEntity} über
 * Spring Data JPA bereit.
 */
public interface AvailableRoadRepository extends JpaRepository<AvailableRoadEntity, String> {
}
