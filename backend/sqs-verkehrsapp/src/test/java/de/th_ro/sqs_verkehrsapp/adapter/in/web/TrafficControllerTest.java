package de.th_ro.sqs_verkehrsapp.adapter.in.web;

import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrafficController.class)
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

        when(trafficQueryUseCase.getTrafficEvents("A1"))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/traffic/A1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("id-1"))
                .andExpect(jsonPath("$[0].roadId").value("A1"))
                .andExpect(jsonPath("$[0].title").value("Title"))
                .andExpect(jsonPath("$[0].subtitle").value("Subtitle"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].type").value("WARNING"))
                .andExpect(jsonPath("$[0].latitude").value(50.123))
                .andExpect(jsonPath("$[0].longitude").value(8.456))
                .andExpect(jsonPath("$[0].riskLevel").value("MEDIUM"));

        verify(trafficQueryUseCase).getTrafficEvents("A1");
    }

    @Test
    void shouldReturnEmptyArrayWhenNoTrafficEventsExist() throws Exception {
        when(trafficQueryUseCase.getTrafficEvents("A2"))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/traffic/A2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
