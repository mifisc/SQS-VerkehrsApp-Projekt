package de.th_ro.sqs_verkehrsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoordinateDto {

    private String lat;

    @JsonProperty("long")
    private String longValue;
}
