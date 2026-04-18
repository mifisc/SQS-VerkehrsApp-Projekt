package de.th_ro.sqs_verkehrsapp.service;

import de.th_ro.sqs_verkehrsapp.client.AutobahnApiClient;
import de.th_ro.sqs_verkehrsapp.dto.TrafficEventResponse;
import de.th_ro.sqs_verkehrsapp.mapper.TrafficEventMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrafficEventService {

    private final AutobahnApiClient autobahnApiClient;
    private final TrafficEventMapper trafficEventMapper;

    public TrafficEventService(AutobahnApiClient autobahnApiClient,
                               TrafficEventMapper trafficEventMapper) {
        this.autobahnApiClient = autobahnApiClient;
        this.trafficEventMapper = trafficEventMapper;
    }

    public List<TrafficEventResponse> getAllTrafficEvents(String roadId) {
        List<TrafficEventResponse> result = new ArrayList<>();

        autobahnApiClient.getClosures(roadId).getClosures()
                .stream()
                .map(trafficEventMapper::fromClosure)
                .forEach(result::add);

        autobahnApiClient.getRoadworks(roadId).getRoadworks()
                .stream()
                .map(trafficEventMapper::fromRoadwork)
                .forEach(result::add);

        autobahnApiClient.getWarnings(roadId).getWarnings()
                .stream()
                .map(trafficEventMapper::fromWarning)
                .forEach(result::add);

        return result;
    }
}
