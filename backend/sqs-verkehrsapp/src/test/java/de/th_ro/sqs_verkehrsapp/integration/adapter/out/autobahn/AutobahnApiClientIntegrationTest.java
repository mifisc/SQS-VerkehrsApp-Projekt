package de.th_ro.sqs_verkehrsapp.integration.adapter.out.autobahn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.AutobahnApiClient;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.AutobahnApiMapper;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.ChargingStationResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.ClosureResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.RoadworksResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.WarningResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.RoadEventCacheAdapter;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

public class AutobahnApiClientIntegrationTest {

    private MockWebServer mockWebServer;
    private AutobahnApiMapper mapper;
    private RoadEventCacheAdapter cacheAdapter;
    private AutobahnApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        mapper = mock(AutobahnApiMapper.class);
        cacheAdapter = mock(RoadEventCacheAdapter.class);

        client = new AutobahnApiClient(webClient, mapper, cacheAdapter);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchTrafficEvents_shouldCallAllEndpointsAndCombineResults() throws Exception {
        mockWebServer.enqueue(jsonResponse("{}"));
        mockWebServer.enqueue(jsonResponse("{}"));
        mockWebServer.enqueue(jsonResponse("{}"));
        mockWebServer.enqueue(jsonResponse("{}"));

        RoadEvent warning = mock(RoadEvent.class);
        RoadEvent roadwork = mock(RoadEvent.class);
        RoadEvent closure = mock(RoadEvent.class);
        RoadEvent chargingStation = mock(RoadEvent.class);

        when(mapper.mapWarnings(eq("A1"), any(WarningResponse.class)))
                .thenReturn(List.of(warning));
        when(mapper.mapRoadworks(eq("A1"), any(RoadworksResponse.class)))
                .thenReturn(List.of(roadwork));
        when(mapper.mapClosures(eq("A1"), any(ClosureResponse.class)))
                .thenReturn(List.of(closure));
        when(mapper.mapChargingStations(eq("A1"), any(ChargingStationResponse.class)))
                .thenReturn(List.of(chargingStation));

        List<RoadEvent> result = client.fetchTrafficEvents("A1");

        assertThat(result)
                .containsExactly(warning, roadwork, closure, chargingStation);

        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A1/services/warning");
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A1/services/roadworks");
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A1/services/closure");
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A1/services/electric_charging_station");
    }

    @Test
    void fetchWarnings_shouldCallWarningEndpointAndMapResponse() throws Exception {
        mockWebServer.enqueue(jsonResponse("{}"));

        RoadEvent warning = mock(RoadEvent.class);

        when(mapper.mapWarnings(eq("A3"), any(WarningResponse.class)))
                .thenReturn(List.of(warning));

        List<RoadEvent> result = client.fetchWarnings("A3");

        assertThat(result).containsExactly(warning);
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A3/services/warning");

        verify(mapper).mapWarnings(eq("A3"), any(WarningResponse.class));
    }

    @Test
    void fetchRoadworks_shouldCallRoadworksEndpointAndMapResponse() throws Exception {
        mockWebServer.enqueue(jsonResponse("{}"));

        RoadEvent roadwork = mock(RoadEvent.class);

        when(mapper.mapRoadworks(eq("A7"), any(RoadworksResponse.class)))
                .thenReturn(List.of(roadwork));

        List<RoadEvent> result = client.fetchRoadworks("A7");

        assertThat(result).containsExactly(roadwork);
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A7/services/roadworks");

        verify(mapper).mapRoadworks(eq("A7"), any(RoadworksResponse.class));
    }

    @Test
    void fetchClosures_shouldCallClosureEndpointAndMapResponse() throws Exception {
        mockWebServer.enqueue(jsonResponse("{}"));

        RoadEvent closure = mock(RoadEvent.class);

        when(mapper.mapClosures(eq("A8"), any(ClosureResponse.class)))
                .thenReturn(List.of(closure));

        List<RoadEvent> result = client.fetchClosures("A8");

        assertThat(result).containsExactly(closure);
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A8/services/closure");

        verify(mapper).mapClosures(eq("A8"), any(ClosureResponse.class));
    }

    @Test
    void fetchChargingStations_shouldCallChargingStationEndpointAndMapResponse() throws Exception {
        mockWebServer.enqueue(jsonResponse("{}"));

        RoadEvent chargingStation = mock(RoadEvent.class);

        when(mapper.mapChargingStations(eq("A9"), any(ChargingStationResponse.class)))
                .thenReturn(List.of(chargingStation));

        List<RoadEvent> result = client.fetchChargingStations("A9");

        assertThat(result).containsExactly(chargingStation);
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A9/services/electric_charging_station");

        verify(mapper).mapChargingStations(eq("A9"), any(ChargingStationResponse.class));
    }

    private MockResponse jsonResponse(String body) {
        return new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(body);
    }
}
