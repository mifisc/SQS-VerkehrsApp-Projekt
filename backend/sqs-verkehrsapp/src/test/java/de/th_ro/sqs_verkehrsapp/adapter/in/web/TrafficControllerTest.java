package de.th_ro.sqs_verkehrsapp.adapter.in.web;

import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.domain.exception.TrafficDataUnavailableException;
import de.th_ro.sqs_verkehrsapp.domain.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({TrafficController.class, GlobalExceptionHandler.class})
class TrafficControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrafficQueryUseCase trafficQueryUseCase;

    @Test
    void shouldReturnTrafficEventsForRoadId() throws Exception {
        RoadEvent event = new RoadEvent(
                "id-1",
                "A1",
                "Title",
                "Subtitle",
                "Description",
                RoadEventType.WARNING,
                new Coordinate(50.123, 8.456),
                RiskLevel.MEDIUM
        );

        TrafficEventsResult result = new TrafficEventsResult(
                List.of(event),
                true,
                LocalDateTime.of(2026, 5, 9, 14, 30)
        );


        when(trafficQueryUseCase.getTrafficEvents("A1"))
                .thenReturn(result);

        mockMvc.perform(get("/api/traffic/A1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.events.length()").value(1))
                .andExpect(jsonPath("$.live").value(true))
                .andExpect(jsonPath("$.cachedAt").value("2026-05-09T14:30:00"))
                .andExpect(jsonPath("$.events.length()").value(1))
                .andExpect(jsonPath("$.events[0].id").value("id-1"))
                .andExpect(jsonPath("$.events[0].roadId").value("A1"))
                .andExpect(jsonPath("$.events[0].title").value("Title"))
                .andExpect(jsonPath("$.events[0].subtitle").value("Subtitle"))
                .andExpect(jsonPath("$.events[0].description").value("Description"))
                .andExpect(jsonPath("$.events[0].type").value("WARNING"))
                .andExpect(jsonPath("$.events[0].latitude").value(50.123))
                .andExpect(jsonPath("$.events[0].longitude").value(8.456))
                .andExpect(jsonPath("$.events[0].riskLevel").value("MEDIUM"));

        verify(trafficQueryUseCase).getTrafficEvents("A1");
    }

    @Test
    void shouldReturnEmptyArrayWhenNoTrafficEventsExist() throws Exception {
        TrafficEventsResult result = new TrafficEventsResult(
                List.of(),
                false,
                null
        );

        when(trafficQueryUseCase.getTrafficEvents("A2"))
                .thenReturn(result);

        mockMvc.perform(get("/api/traffic/A2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.live").value(false))
                .andExpect(jsonPath("$.cachedAt").doesNotExist())
                .andExpect(jsonPath("$.events.length()").value(0));
    }

    @Test
    void shouldReturn503WhenTrafficDataUnavailable() throws Exception {
        when(trafficQueryUseCase.getTrafficEvents("A8"))
                .thenThrow(new TrafficDataUnavailableException(
                        "Autobahn API nicht verfügbar und keine Cache-Daten vorhanden für A8",
                        new RuntimeException()
                ));

        mockMvc.perform(get("/api/traffic/A8"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.code").value("TRAFFIC_DATA_UNAVAILABLE"))
                .andExpect(jsonPath("$.message").value(
                        "Autobahn API nicht verfügbar und keine Cache-Daten vorhanden für A8"
                ))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
