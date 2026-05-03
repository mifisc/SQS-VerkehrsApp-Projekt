package de.th_ro.sqs_verkehrsapp.service;

/*
@ExtendWith(MockitoExtension.class)
class TrafficServiceTest {

    @Mock
    private AutobahnApiClient autobahnApiClient;

    @InjectMocks
    private TrafficService trafficService;

    @Disabled
    @Test
    void loadRoadworks() {
        RoadworkDto dto = new RoadworkDto();
        dto.setTitle("A1 | Test Roadwork");

        RoadworksResponse response = new RoadworksResponse();
        response.setRoadworks(List.of(dto));

        when(autobahnApiClient.getRoadworks("A1")).thenReturn(response);

        List<RoadEvent> result = trafficService.getTrafficEvents("A1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("A1 | Test Roadwork");
    }
}*/
