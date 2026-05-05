package de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi;

import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.AutobahnApiClient;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.AutobahnApiMapper;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.ChargingStationResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.ClosureResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.RoadworksResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.WarningResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.RoadEventCacheAdapter;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutobahnApiClientTest {

    @Mock
    private AutobahnApiMapper mapper;

    @Mock
    private RoadEventCacheAdapter cacheAdapter;

    private MockWebServer mockWebServer;

    private AutobahnApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        client = new AutobahnApiClient(webClient, mapper, cacheAdapter);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void fetchTrafficEvents_shouldFetchAndCombineAllEventTypes() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("""
                        {
                          "warning": []
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("""
                        {
                          "roadworks": []
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("""
                        {
                          "closure": []
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("""
                        {
                          "electric_charging_station": []
                        }
                        """)
                .addHeader("Content-Type", "application/json"));

        RoadEvent warning = new RoadEvent(
                "warning-1",
                "A1",
                "Warnung",
                "Achtung",
                "",
                RoadEventType.WARNING,
                new Coordinate(52.1, 13.4),
                null
        );

        RoadEvent roadwork = new RoadEvent(
                "roadwork-1",
                "A1",
                "Baustelle",
                "Baustelle voraus",
                "",
                RoadEventType.ROADWORK,
                new Coordinate(52.2, 13.5),
                null
        );

        RoadEvent closure = new RoadEvent(
                "closure-1",
                "A1",
                "Sperrung",
                "gesperrt",
                "",
                RoadEventType.CLOSURE,
                new Coordinate(52.3, 13.6),
                null
        );

        RoadEvent charging = new RoadEvent(
                "charging-1",
                "A1",
                "Ladesäule",
                "frei",
                "",
                RoadEventType.CHARGING_STATION,
                new Coordinate(52.4, 13.7),
                null
        );

        when(mapper.mapWarnings(eq("A1"), any(WarningResponse.class)))
                .thenReturn(List.of(warning));

        when(mapper.mapRoadworks(eq("A1"), any(RoadworksResponse.class)))
                .thenReturn(List.of(roadwork));

        when(mapper.mapClosures(eq("A1"), any(ClosureResponse.class)))
                .thenReturn(List.of(closure));

        when(mapper.mapChargingStations(eq("A1"), any(ChargingStationResponse.class)))
                .thenReturn(List.of(charging));

        List<RoadEvent> result = client.fetchTrafficEvents("A1");

        assertEquals(4, result.size());
        assertTrue(result.contains(warning));
        assertTrue(result.contains(roadwork));
        assertTrue(result.contains(closure));
        assertTrue(result.contains(charging));
    }
}
