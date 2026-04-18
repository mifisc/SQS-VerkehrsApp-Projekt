package de.th_ro.sqs_verkehrsapp.dto.external.wrapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ClosureResponseTest {

    @Autowired
    private JacksonTester<ClosureResponse> json;

    @Test
    void shouldDeserializeClosureResponse() throws Exception {
        String content = """
            {
              "closure": [
                {
                  "identifier": "id-1",
                  "title": "A1 | Test",
                  "display_type": "WEIGHT_LIMIT_35",
                  "coordinate": {
                    "lat": "51.036440",
                    "long": "6.964910"
                  }
                }
              ]
            }
            """;

        ClosureResponse response = json.parseObject(content);

        assertThat(response.getClosures()).hasSize(1);
        assertThat(response.getClosures().get(0).getDisplayType()).isEqualTo("WEIGHT_LIMIT_35");
        assertThat(response.getClosures().get(0).getCoordinate().getLongValue()).isEqualTo("6.964910");
    }

}