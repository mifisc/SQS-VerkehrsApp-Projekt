package de.th_ro.sqs_verkehrsapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChargingStationDto {

    private String extent;
    private String identifier;

    private List<String> routeRecommendation;

    private Coordinate coordinate;

    private List<String> footer;

    private String icon;
    private String isBlocked;

    private List<String> description;

    private String title;
    private String point;

    @JsonProperty("display_type")
    private String displayType;

    private List<String> lorryParkingFeatureIcons;

    private boolean future;
    private String subtitle;
}
