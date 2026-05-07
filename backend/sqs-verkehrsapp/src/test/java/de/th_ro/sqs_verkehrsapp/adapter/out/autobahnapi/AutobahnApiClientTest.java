package de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper.ClosureResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper.RoadworksResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper.WarningResponse;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import java.io.IOException;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class AutobahnApiClientTest {

    private MockWebServer mockWebServer;
    private AutobahnApiMapper mapper;
    private AutobahnApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        mapper = mock(AutobahnApiMapper.class);
        client = new AutobahnApiClient(webClient, mapper);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldFetchRoadworksAndMapResponse() throws Exception {
        mockWebServer.enqueue(json("""
                {
                  "roadworks": [
                    {
                      "identifier": "r1",
                      "title": "Roadwork",
                      "subtitle": "A subtitle",
                      "description": ["Desc"],
                      "coordinate": {
                        "lat": "50.0",
                        "long": "8.0"
                      }
                    }
                  ]
                }
                """));

        List<RoadEvent> expected = List.of(event("r1", RoadEventType.ROADWORK));
        when(mapper.mapRoadworks(eq("A1"), any(RoadworksResponse.class)))
                .thenReturn(expected);

        List<RoadEvent> result = client.getRoadworks("A1");

        assertThat(result).isEqualTo(expected);
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A1/services/roadworks");

        verify(mapper).mapRoadworks(eq("A1"), any(RoadworksResponse.class));
    }

    @Test
    void shouldFetchWarningsAndMapResponse() throws Exception {
        mockWebServer.enqueue(json("""
                {
                  "warning": [
                    {
                      "identifier": "w1",
                      "title": "Warning",
                      "coordinate": {
                        "lat": "50.0",
                        "long": "8.0"
                      }
                    }
                  ]
                }
                """));

        List<RoadEvent> expected = List.of(event("w1", RoadEventType.WARNING));
        when(mapper.mapWarnings(eq("A2"), any(WarningResponse.class)))
                .thenReturn(expected);

        List<RoadEvent> result = client.getWarnings("A2");

        assertThat(result).isEqualTo(expected);
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A2/services/warning");
    }

    @Test
    void shouldFetchClosuresAndMapResponse() throws Exception {
        mockWebServer.enqueue(json("""
                {
                  "closure": [
                    {
                      "identifier": "c1",
                      "title": "Closure",
                      "coordinate": {
                        "lat": "50.0",
                        "long": "8.0"
                      }
                    }
                  ]
                }
                """));

        List<RoadEvent> expected = List.of(event("c1", RoadEventType.CLOSURE));
        when(mapper.mapClosures(eq("A3"), any(ClosureResponse.class)))
                .thenReturn(expected);

        List<RoadEvent> result = client.getClosures("A3");

        assertThat(result).isEqualTo(expected);
        assertThat(mockWebServer.takeRequest().getPath())
                .isEqualTo("/A3/services/closure");
    }

    private MockResponse json(String body) {
        return new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(body);
    }

    private RoadEvent event(String id, RoadEventType type) {
        return new RoadEvent(
                id,
                "A1",
                "Title",
                "Subtitle",
                "Description",
                type,
                new Coordinate(50.0, 8.0),
                RiskLevel.MEDIUM
        );
    }
}
