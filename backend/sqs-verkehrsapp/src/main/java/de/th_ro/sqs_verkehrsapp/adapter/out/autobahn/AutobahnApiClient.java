package de.th_ro.sqs_verkehrsapp.adapter.out.autobahn;

import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.ChargingStationResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.ClosureResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.RoadworksResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.WarningResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.RoadEventCacheAdapter;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class AutobahnApiClient {

    private final WebClient webClient;
    private final AutobahnApiMapper mapper;
    private final RoadEventCacheAdapter cacheAdapter;

    public AutobahnApiClient(WebClient webClient, AutobahnApiMapper mapper, RoadEventCacheAdapter cacheAdapter) {
        this.webClient = webClient;
        this.mapper = mapper;
        this.cacheAdapter = cacheAdapter;
    }

    public List<RoadEvent> fetchTrafficEvents(String roadId) {
        List<RoadEvent> events = new ArrayList<>();

        events.addAll(fetchWarnings(roadId));
        events.addAll(fetchRoadworks(roadId));
        events.addAll(fetchClosures(roadId));
        events.addAll(fetchChargingStations(roadId));

        return events;
    }


    public List<RoadEvent> fetchRoadworks(String roadId) {
        RoadworksResponse roadworksResponse = webClient.get()
                .uri("/{roadId}/services/roadworks", roadId)
                .retrieve()
                .bodyToMono(RoadworksResponse.class)
                .block();

        return mapper.mapRoadworks(roadId, roadworksResponse);
    }

    public List<RoadEvent> fetchWarnings(String roadId) {
        WarningResponse warningResponse = webClient.get()
                .uri("/{roadId}/services/warning", roadId)
                .retrieve()
                .bodyToMono(WarningResponse.class)
                .block();
        return mapper.mapWarnings(roadId, warningResponse);
    }

    public List<RoadEvent> fetchClosures(String roadId) {

        ClosureResponse closureResponse = webClient.get()
                .uri("/{roadId}/services/closure", roadId)
                .retrieve()
                .bodyToMono(ClosureResponse.class)
                .block();
        return mapper.mapClosures(roadId, closureResponse);
    }

    public List<RoadEvent> fetchChargingStations(String roadId) {

        ChargingStationResponse chargingStationResponse = webClient.get()
                .uri("/{roadId}/services/electric_charging_station", roadId)
                .retrieve()
                .bodyToMono(ChargingStationResponse.class)
                .block();

        return mapper.mapChargingStations(roadId, chargingStationResponse);
    }

    //Is used in ResilientAutobahnApiAdapter als Fallbackmethod
    public List<RoadEvent> getTrafficEventsFallback(String roadId, Throwable throwable) {
        return cacheAdapter.findByRoadId(roadId);
    }
}
