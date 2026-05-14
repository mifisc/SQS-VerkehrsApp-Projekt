package de.th_ro.sqs_verkehrsapp.integration.adapter.in.web;

import de.th_ro.sqs_verkehrsapp.adapter.in.web.TrafficController;
import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(TrafficController.class)
public class TrafficControllerIntegrationTest {


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

        TrafficEventsResult result = new TrafficEventsResult(
                events,
                true,
                LocalDateTime.of(2026, 5, 9, 14, 30)
        );

        when(trafficQueryUseCase.getTrafficEvents("A1"))
                .thenReturn(result);;

        mockMvc.perform(get("/api/traffic/A1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.live").value(true))
                .andExpect(jsonPath("$.cachedAt")
                        .value("2026-05-09T14:30:00"))
                .andExpect(jsonPath("$.events.length()").value(3))
                .andExpect(jsonPath("$.events[0].id").value("w1"))
                .andExpect(jsonPath("$.events[1].id").value("r1"))
                .andExpect(jsonPath("$.events[2].id").value("c1"));
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
