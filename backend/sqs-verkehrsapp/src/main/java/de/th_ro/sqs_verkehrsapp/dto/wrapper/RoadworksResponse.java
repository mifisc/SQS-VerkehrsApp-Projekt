package de.th_ro.sqs_verkehrsapp.dto.wrapper;

import de.th_ro.sqs_verkehrsapp.dto.RoadworkDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoadworksResponse {
    private List<RoadworkDto> roadworks;
}
