package de.th_ro.sqs_verkehrsapp.service;

import de.th_ro.sqs_verkehrsapp.client.AutobahnApiClient;
import de.th_ro.sqs_verkehrsapp.dto.external.ChargingStationDto;
import de.th_ro.sqs_verkehrsapp.dto.external.ClosureDto;
import de.th_ro.sqs_verkehrsapp.dto.external.RoadworkDto;
import de.th_ro.sqs_verkehrsapp.dto.external.WarningDto;
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

    public List<WarningDto> loadWarnings(String roadId) {
        return autobahnApiClient.getWarnings(roadId).getWarnings();
    }

    public List<ClosureDto> loadClosures(String roadId) {
        return autobahnApiClient.getClosures(roadId).getClosures();
    }

    public List<ChargingStationDto> loadChargingStations(String roadId) {
        return autobahnApiClient.getChargingStations(roadId).getElectricChargingStations();
    }
}
