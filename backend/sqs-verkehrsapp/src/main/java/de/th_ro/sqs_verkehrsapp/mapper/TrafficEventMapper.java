package de.th_ro.sqs_verkehrsapp.mapper;

import de.th_ro.sqs_verkehrsapp.dto.TrafficEventResponse;
import de.th_ro.sqs_verkehrsapp.dto.external.ClosureDto;
import de.th_ro.sqs_verkehrsapp.dto.external.Coordinate;
import de.th_ro.sqs_verkehrsapp.dto.external.RoadworkDto;
import de.th_ro.sqs_verkehrsapp.dto.external.WarningDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TrafficEventMapper {

    public TrafficEventResponse fromClosure(ClosureDto dto) {
        return TrafficEventResponse.builder()
                .id(dto.getIdentifier())
                .type("CLOSURE")
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .description(joinDescription(dto.getDescription()))
                .lat(parseLat(dto.getCoordinate()))
                .lon(parseLon(dto.getCoordinate()))
                .blocked(parseBlocked(dto.getIsBlocked()))
                .future(dto.isFuture())
                .icon(dto.getIcon())
                .severity(determineSeverity("CLOSURE", parseBlocked(dto.getIsBlocked())))
                .build();
    }

    public TrafficEventResponse fromRoadwork(RoadworkDto dto) {
        return TrafficEventResponse.builder()
                .id(dto.getIdentifier())
                .type("ROADWORK")
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .description(joinDescription(dto.getDescription()))
                .lat(parseLat(dto.getCoordinate()))
                .lon(parseLon(dto.getCoordinate()))
                .blocked(parseBlocked(dto.getIsBlocked()))
                .future(dto.isFuture())
                .icon(dto.getIcon())
                .startTimestamp(dto.getStartTimestamp())
                .severity(determineSeverity("ROADWORK", parseBlocked(dto.getIsBlocked())))
                .build();
    }

    public TrafficEventResponse fromWarning(WarningDto dto) {
        return TrafficEventResponse.builder()
                .id(dto.getIdentifier())
                .type("WARNING")
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .description(joinDescription(dto.getDescription()))
                .lat(parseLat(dto.getCoordinate()))
                .lon(parseLon(dto.getCoordinate()))
                .blocked(parseBlocked(dto.getIsBlocked()))
                .future(dto.isFuture())
                .icon(dto.getIcon())
                .startTimestamp(dto.getStartTimestamp())
                .severity(determineSeverity("WARNING", parseBlocked(dto.getIsBlocked())))
                .build();
    }

    private String joinDescription(List<String> descriptions) {
        if (descriptions == null || descriptions.isEmpty()) {
            return "";
        }

        return descriptions.stream()
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" | "));
    }

    private boolean parseBlocked(String isBlocked) {
        return "true".equalsIgnoreCase(isBlocked);
    }

    private Double parseLat(Coordinate coordinate) {
        if (coordinate == null || coordinate.getLat() == null || coordinate.getLat().isBlank()) {
            return null;
        }

        return Double.valueOf(coordinate.getLat());
    }

    private Double parseLon(Coordinate coordinate) {
        if (coordinate == null || coordinate.getLongValue() == null || coordinate.getLongValue().isBlank()) {
            return null;
        }

        return Double.valueOf(coordinate.getLongValue());
    }

    private String determineSeverity(String type, boolean blocked) {
        return switch (type) {
            case "CLOSURE" -> blocked ? "HIGH" : "MEDIUM";
            case "ROADWORK" -> blocked ? "MEDIUM" : "LOW";
            case "WARNING" -> "MEDIUM";
            default -> "LOW";
        };
    }
}
