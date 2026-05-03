package de.th_ro.sqs_verkehrsapp.client;

/*
class AutobahnApiClientTest {

    private MockWebServer mockWebServer;
    private AutobahnApiClient autobahnApiClient;

    @BeforeEach
    void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        AutobahnApiProperties properties = new AutobahnApiProperties();
        properties.setBaseUrl(mockWebServer.url("/o/autobahn").toString());

        autobahnApiClient = new AutobahnApiClient(properties);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    void getClosures_shouldMapResponseCorrectly() {
        String json = """
            {
              "closure": [
                {
                  "identifier": "abc123",
                  "title": "A1 | Test",
                  "subtitle": "Richtung Köln",
                  "display_type": "WEIGHT_LIMIT_35",
                  "future": false
                }
              ]
            }
            """;

        mockWebServer.enqueue(new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(json));

        ClosureResponse response = autobahnApiClient.getClosures("A1");

        assertThat(response).isNotNull();
        assertThat(response.getClosures()).hasSize(1);
        assertThat(response.getClosures().get(0).getTitle()).isEqualTo("A1 | Test");
        assertThat(response.getClosures().get(0).getDisplayType()).isEqualTo("WEIGHT_LIMIT_35");
    }

}*/
