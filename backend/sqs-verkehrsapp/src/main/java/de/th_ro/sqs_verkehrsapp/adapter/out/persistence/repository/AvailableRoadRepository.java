package de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.AvailableRoadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvailableRoadRepository extends JpaRepository<AvailableRoadEntity, String> {
}
