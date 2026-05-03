package de.th_ro.sqs_verkehrsapp.dto.wrapper;

import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper.WarningResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class WarningResponseTest {

    @Autowired
    private JacksonTester<WarningResponse> json;

    @Test
    void shouldDeserializeWarningResponse() throws Exception {
        String content = """
            {
              "warning": [
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

        WarningResponse response = json.parseObject(content);

        assertThat(response.getWarnings()).hasSize(1);
        assertThat(response.getWarnings().get(0).getDisplayType()).isEqualTo("WEIGHT_LIMIT_35");
        assertThat(response.getWarnings().get(0).getCoordinateDto().getLongValue()).isEqualTo("6.964910");
    }

}