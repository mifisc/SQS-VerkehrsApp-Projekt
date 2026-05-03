package de.th_ro.sqs_verkehrsapp.dto.wrapper;

import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper.ChargingStationResponse;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ChargingStationResponseTest {

    @Autowired
    private JacksonTester<ChargingStationResponse> json;

    @Test
    void shouldDeserializeChargingStationResponse() throws Exception {
        String content = """
            {
              "electric_charging_station": [
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

        ChargingStationResponse response = json.parseObject(content);

        assertThat(response.getElectricChargingStations()).hasSize(1);
        AssertionsForClassTypes.assertThat(response.getElectricChargingStations().get(0).getDisplayType()).isEqualTo("WEIGHT_LIMIT_35");
        AssertionsForClassTypes.assertThat(response.getElectricChargingStations().get(0).getCoordinateDto().getLongValue()).isEqualTo("6.964910");
    }


}