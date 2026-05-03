package de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi;

import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper.ChargingStationResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper.ClosureResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper.RoadworksResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper.WarningResponse;
import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class AutobahnApiClient implements AutobahnApiPort {

    private final WebClient webClient;
    private final AutobahnApiMapper mapper;

    public AutobahnApiClient(WebClient webClient, AutobahnApiMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }


    @Override
    public List<RoadEvent> getRoadworks(String roadId) {
        RoadworksResponse roadworksResponse = webClient.get()
                .uri("/{roadId}/services/roadworks", roadId)
                .retrieve()
                .bodyToMono(RoadworksResponse.class)
                .block();

        return mapper.mapRoadworks(roadId, roadworksResponse);
    }

    @Override
    public List<RoadEvent> getWarnings(String roadId) {
        WarningResponse warningResponse = webClient.get()
                .uri("/{roadId}/services/warning", roadId)
                .retrieve()
                .bodyToMono(WarningResponse.class)
                .block();
        return mapper.mapWarnings(roadId, warningResponse);
    }

    @Override
    public List<RoadEvent> getClosures(String roadId) {

        ClosureResponse closureResponse = webClient.get()
                .uri("/{roadId}/services/closure", roadId)
                .retrieve()
                .bodyToMono(ClosureResponse.class)
                .block();
        return mapper.mapClosures(roadId, closureResponse);
    }

    @Override
    public List<RoadEvent> getChargingStations(String roadId) {

        ChargingStationResponse chargingStationResponse = webClient.get()
                .uri("/{roadId}/services/electric_charging_station", roadId)
                .retrieve()
                .bodyToMono(ChargingStationResponse.class)
                .block();

        return mapper.mapChargingStations(roadId, chargingStationResponse);
    }
}
