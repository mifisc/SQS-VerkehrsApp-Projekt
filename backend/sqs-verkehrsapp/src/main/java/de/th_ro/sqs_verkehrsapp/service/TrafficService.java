package de.th_ro.sqs_verkehrsapp.service;

import de.th_ro.sqs_verkehrsapp.client.AutobahnApiClient;
import de.th_ro.sqs_verkehrsapp.dto.RoadworkDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrafficService {

    private final AutobahnApiClient autobahnApiClient;

    public TrafficService(AutobahnApiClient autobahnApiClient) {
        this.autobahnApiClient = autobahnApiClient;
    }

    public List<RoadworkDto> loadRoadworks(String roadId) {
        return autobahnApiClient.getRoadworks(roadId).getRoadworks();
    }

    public String loadWarnings(String roadId) {
        return autobahnApiClient.getWarnings(roadId);
    }

    public String loadClosures(String roadId) {
        return autobahnApiClient.getClosures(roadId);
    }

    public String loadChargingStations(String roadId) {
        return autobahnApiClient.getChargingStations(roadId);
    }
}
