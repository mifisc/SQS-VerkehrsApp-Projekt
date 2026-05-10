package de.th_ro.sqs_verkehrsapp.application.service;

import de.th_ro.sqs_verkehrsapp.application.port.in.DashboardTrafficUseCase;
import de.th_ro.sqs_verkehrsapp.application.port.out.SavedRoadRepositoryPort;
import de.th_ro.sqs_verkehrsapp.domain.model.SavedRoad;
import de.th_ro.sqs_verkehrsapp.domain.model.SavedRoadTrafficResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardTrafficService implements DashboardTrafficUseCase {

    private final SavedRoadRepositoryPort savedRoadRepositoryPort;
    private final TrafficService trafficService;

    @Override
    public List<SavedRoadTrafficResult> getTrafficForSavedRoads(UUID userId) {

        List<SavedRoad> savedRoads =
                savedRoadRepositoryPort.findByUserId(userId);

        return savedRoads.stream()
                .map(savedRoad -> new SavedRoadTrafficResult(
                        savedRoad.getRoadId(),
                        trafficService.getTrafficEvents(savedRoad.getRoadId())
                ))
                .toList();
    }
}
