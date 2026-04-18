package de.th_ro.sqs_verkehrsapp.controller;

import de.th_ro.sqs_verkehrsapp.dto.ClosureDto;
import de.th_ro.sqs_verkehrsapp.service.TrafficService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TrafficController.class)
class TrafficControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrafficService trafficService;

    @Test
    void getClosures_shouldReturnJsonArray() throws Exception {
        ClosureDto dto = new ClosureDto();
        dto.setTitle("A1 | AK Leverkusen-West - AS Köln-Niehl");
        dto.setSubtitle("Dortmund Richtung Köln");

        when(trafficService.loadClosures("A1")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/roads/A1/closures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("A1 | AK Leverkusen-West - AS Köln-Niehl"))
                .andExpect(jsonPath("$[0].subtitle").value("Dortmund Richtung Köln"));
    }

}