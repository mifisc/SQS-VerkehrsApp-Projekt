package de.th_ro.sqs_verkehrsapp.application.service;

import de.th_ro.sqs_verkehrsapp.application.port.in.SavedRoadUseCase;
import de.th_ro.sqs_verkehrsapp.application.port.out.SavedRoadRepositoryPort;
import de.th_ro.sqs_verkehrsapp.domain.model.SavedRoad;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavedRoadService implements SavedRoadUseCase {

    private final SavedRoadRepositoryPort savedRoadRepositoryPort;

    @Override
    public SavedRoad saveRoad(UUID userId, String roadId) {

        String normalizedRoadId = roadId.trim().toUpperCase();

        if (savedRoadRepositoryPort.existsByUserIdAndRoadId(userId, normalizedRoadId)) {
            throw new IllegalArgumentException("Diese Autobahn wurde bereits gespeichert");
        }

        SavedRoad savedRoad = SavedRoad.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .roadId(normalizedRoadId)
                .build();

        return savedRoadRepositoryPort.save(savedRoad);
    }

    @Override
    public List<SavedRoad> getSavedRoads(UUID userId) {
        return savedRoadRepositoryPort.findByUserId(userId);
    }

    @Override
    public void deleteRoad(UUID userId, String roadId) {
        savedRoadRepositoryPort.deleteByUserIdAndRoadId(
                userId,
                roadId.trim().toUpperCase()
        );
    }
}
