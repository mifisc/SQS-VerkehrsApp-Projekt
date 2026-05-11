package de.th_ro.sqs_verkehrsapp.adapter.out.persistence;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.AvailableRoadEntity;
import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository.AvailableRoadRepository;
import de.th_ro.sqs_verkehrsapp.application.port.out.AvailableRoadCachePort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AvailableRoadsCacheAdapter implements AvailableRoadCachePort {

    private final AvailableRoadRepository repository;

    public AvailableRoadsCacheAdapter(AvailableRoadRepository repository) {
        this.repository = repository;
    }

    @Override
    public void saveAll(List<String> roadIds) {
        repository.deleteAll();

        List<AvailableRoadEntity> entities = roadIds.stream()
                .map(AvailableRoadEntity::new)
                .toList();

        repository.saveAll(entities);
    }

    @Override
    public List<String> findAll() {
        return repository.findAll()
                .stream()
                .map(AvailableRoadEntity::getRoadId)
                .toList();
    }
}
