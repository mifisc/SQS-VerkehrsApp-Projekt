package de.th_ro.sqs_verkehrsapp.external;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentCacheEntryRepository extends JpaRepository<IncidentCacheEntry, Long> {
    Optional<IncidentCacheEntry> findByRoadIdAndCategory(String roadId, String category);
}
