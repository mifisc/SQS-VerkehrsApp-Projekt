package de.th_ro.sqs_verkehrsapp.adapter.out.autobahnapi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoadworkDto  extends BaseAutobahnDto {

    private String identifier;
    private String title;
    private String subtitle;
    private String icon;
    private String isBlocked;
    private boolean future;

    private String extent;
    private String point;

    private CoordinateDto coordinateDto;

    private List<String> description;

    private String displayType;
    private String startTimestamp;
}
