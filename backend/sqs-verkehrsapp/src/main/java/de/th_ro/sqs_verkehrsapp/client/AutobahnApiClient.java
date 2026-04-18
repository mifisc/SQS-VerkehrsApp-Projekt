package de.th_ro.sqs_verkehrsapp.client;

import de.th_ro.sqs_verkehrsapp.config.AutobahnApiProperties;
import de.th_ro.sqs_verkehrsapp.dto.wrapper.ChargingStationResponse;
import de.th_ro.sqs_verkehrsapp.dto.wrapper.ClosureResponse;
import de.th_ro.sqs_verkehrsapp.dto.wrapper.RoadworksResponse;
import de.th_ro.sqs_verkehrsapp.dto.wrapper.WarningResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AutobahnApiClient {

    private final WebClient webClient;

    public AutobahnApiClient(AutobahnApiProperties properties) {
        System.out.println("Base URL = " + properties.getBaseUrl());

        this.webClient = WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
                .build();
    }

    public RoadworksResponse getRoadworks(String roadId) {
        return webClient.get()
                .uri("/{roadId}/services/roadworks", roadId)
                .retrieve()
                .bodyToMono(RoadworksResponse.class)
                .block();
    }

    public WarningResponse getWarnings(String roadId) {
        return webClient.get()
                .uri("/{roadId}/services/warning", roadId)
                .retrieve()
                .bodyToMono(WarningResponse.class)
                .block();
    }

    public ClosureResponse getClosures(String roadId) {
        return webClient.get()
                .uri("/{roadId}/services/closure", roadId)
                .retrieve()
                .bodyToMono(ClosureResponse.class)
                .block();
    }

    public ChargingStationResponse getChargingStations(String roadId) {
        return webClient.get()
                .uri("/{roadId}/services/electric_charging_station", roadId)
                .retrieve()
                .bodyToMono(ChargingStationResponse.class)
                .block();
    }
}
