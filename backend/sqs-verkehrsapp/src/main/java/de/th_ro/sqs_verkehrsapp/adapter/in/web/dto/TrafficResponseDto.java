package de.th_ro.sqs_verkehrsapp.adapter.in.web.dto;

import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;

public record TrafficResponseDto(String id,
                                 String roadId,
                                 String title,
                                 String subtitle,
                                 String description,
                                 RoadEventType type,
                                 double latitude,
                                 double longitude,
                                 RiskLevel riskLevel) {
}
