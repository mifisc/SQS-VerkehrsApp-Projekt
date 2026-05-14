package de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto.RoadworkDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoadworksResponse {

    @JsonProperty("roadworks")
    private List<RoadworkDto> roadworks;
}
