package de.th_ro.sqs_verkehrsapp.controller;

/*
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

        when(trafficService.getTrafficEvents("A1")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/roads/A1/closures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("A1 | AK Leverkusen-West - AS Köln-Niehl"))
                .andExpect(jsonPath("$[0].subtitle").value("Dortmund Richtung Köln"));
    }

}*/
