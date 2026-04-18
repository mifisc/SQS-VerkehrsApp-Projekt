package de.th_ro.sqs_verkehrsapp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrafficEventResponse {

    private String id;
    private String type;
    private String title;
    private String subtitle;
    private String description;

    private Double lat;
    private Double lon;

    private boolean blocked;
    private boolean future;

    private String icon;
    private String startTimestamp;
    private String severity;
}
