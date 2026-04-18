package de.th_ro.sqs_verkehrsapp.service;

import de.th_ro.sqs_verkehrsapp.client.AutobahnApiClient;
import org.springframework.stereotype.Service;

@Service
public class TrafficService {

    private final AutobahnApiClient autobahnApiClient;

    public TrafficService(AutobahnApiClient autobahnApiClient) {
        this.autobahnApiClient = autobahnApiClient;
    }

    public String loadRoadworks(String roadId) {
        return autobahnApiClient.getRoadworks(roadId);
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
