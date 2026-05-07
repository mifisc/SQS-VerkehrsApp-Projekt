package de.th_ro.sqs_verkehrsapp.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TrafficIntegrationTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrafficQueryUseCase trafficQueryUseCase;

    @Test
    void shouldReturnTrafficEvents() throws Exception {
        List<RoadEvent> events = List.of(
                event("w1", RoadEventType.WARNING, RiskLevel.MEDIUM),
                event("r1", RoadEventType.ROADWORK, RiskLevel.MEDIUM),
                event("c1", RoadEventType.CLOSURE, RiskLevel.HIGH));

        when(trafficQueryUseCase.getTrafficEvents("A1"))
                .thenReturn(events);;

        mockMvc.perform(get("/api/traffic/A1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value("w1"))
                .andExpect(jsonPath("$[1].id").value("r1"))
                .andExpect(jsonPath("$[2].id").value("c1"));
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
