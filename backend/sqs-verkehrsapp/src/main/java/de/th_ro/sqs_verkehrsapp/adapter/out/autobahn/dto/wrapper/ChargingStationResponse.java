package de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.ChargingStationDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChargingStationResponse {

    @JsonProperty("electric_charging_station")
    private List<ChargingStationDto> electricChargingStations;
}
