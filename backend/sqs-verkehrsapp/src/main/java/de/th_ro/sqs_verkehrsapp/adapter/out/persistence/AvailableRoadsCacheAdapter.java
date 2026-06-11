package de.th_ro.sqs_verkehrsapp.adapter.out.persistence;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.AvailableRoadEntity;
import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository.AvailableRoadRepository;
import de.th_ro.sqs_verkehrsapp.application.port.out.AvailableRoadCachePort;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Persistence-Adapter für den Cache verfügbarer Autobahnen.
 * <p>
 * Implementiert {@link AvailableRoadCachePort} und speichert die von der
 * Autobahn-API bereitgestellten Autobahnkennungen in der Datenbank.
 */
@Component
public class AvailableRoadsCacheAdapter implements AvailableRoadCachePort {

    private final AvailableRoadRepository repository;

    public AvailableRoadsCacheAdapter(AvailableRoadRepository repository) {
        this.repository = repository;
    }

    /**
     * Speichert die übergebenen Autobahnkennungen im Cache.
     * Bereits vorhandene Einträge werden zuvor entfernt.
     *
     * @param roadIds zu speichernde Autobahnkennungen
     */
    @Override
    public void saveAll(List<String> roadIds) {
        repository.deleteAll();

        List<AvailableRoadEntity> entities = roadIds.stream()
                .map(AvailableRoadEntity::new)
                .toList();

        repository.saveAll(entities);
    }

    /**
     * Lädt alle im Cache gespeicherten Autobahnkennungen.
     *
     * @return Liste der verfügbaren Autobahnkennungen
     */
    @Override
    public List<String> findAll() {
        return repository.findAll()
                .stream()
                .map(AvailableRoadEntity::getRoadId)
                .toList();
    }
}
