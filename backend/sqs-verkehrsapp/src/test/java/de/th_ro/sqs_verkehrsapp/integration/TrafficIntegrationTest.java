package de.th_ro.sqs_verkehrsapp.integration;

import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TrafficIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AutobahnApiPort autobahnApiPort;

    @Test
    void shouldReturnTrafficEvents() throws Exception {

        when(autobahnApiPort.getWarnings("A1"))
                .thenReturn(List.of(event("w1", RoadEventType.WARNING, RiskLevel.MEDIUM)));
        when(autobahnApiPort.getRoadworks("A1"))
                .thenReturn(List.of(event("r1", RoadEventType.ROADWORK, RiskLevel.MEDIUM)));
        when(autobahnApiPort.getClosures("A1"))
                .thenReturn(List.of(event("c1", RoadEventType.CLOSURE, RiskLevel.HIGH)));
        when(autobahnApiPort.getChargingStations("A1"))
                .thenReturn(List.of(event("e1", RoadEventType.CHARGING_STATION, RiskLevel.LOW)));

        mockMvc.perform(get("/api/traffic/A1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].id").value("w1"))
                .andExpect(jsonPath("$[1].id").value("r1"))
                .andExpect(jsonPath("$[2].id").value("c1"))
                .andExpect(jsonPath("$[3].id").value("e1"));
    }

    private RoadEvent event(String id, RoadEventType type, RiskLevel riskLevel) {
        return new RoadEvent(
                id,
                "A1",
                "Title " + id,
                "Subtitle " + id,
                "Description " + id,
                type,
                new Coordinate(50.0, 8.0),
                riskLevel
        );
    }
}
