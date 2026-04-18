package de.th_ro.sqs_verkehrsapp.dto.wrapper;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class RoadworksResponseTest {

    @Autowired
    private JacksonTester<RoadworksResponse> json;

    @Test
    void shouldDeserializeRoadworksResponse() throws Exception {
        String content = """
            {
              "roadworks": [
                {
                  "identifier": "id-1",
                  "title": "A1 | Test",
                  "coordinate": {
                    "lat": "51.036440",
                    "long": "6.964910"
                  }
                }
              ]
            }
            """;

        RoadworksResponse response = json.parseObject(content);

        assertThat(response.getRoadworks()).hasSize(1);
        AssertionsForClassTypes.assertThat(response.getRoadworks().get(0).getIdentifier()).isEqualTo("id-1");
        AssertionsForClassTypes.assertThat(response.getRoadworks().get(0).getCoordinate().getLongValue()).isEqualTo("6.964910");
    }

}