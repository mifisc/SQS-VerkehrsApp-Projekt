package de.th_ro.sqs_verkehrsapp.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coordinate {

    private String lat;

    @JsonProperty("long")
    private String longValue;
}
