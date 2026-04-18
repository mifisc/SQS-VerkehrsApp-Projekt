package de.th_ro.sqs_verkehrsapp.incidents;

import de.th_ro.sqs_verkehrsapp.external.DataSourceType;

import java.time.Instant;
import java.util.List;

public record IncidentDto(
        String id,
        String roadId,
        String category,
        String categoryLabel,
        String title,
        String subtitle,
        List<String> description,
        double latitude,
        double longitude,
        boolean blocked,
        boolean future,
        Instant startTimestamp,
        int riskWeight,
        DataSourceType source
) {
    public static IncidentDto from(Incident incident) {
        return new IncidentDto(
                incident.id(),
                incident.roadId(),
                incident.category().name(),
                incident.category().label(),
                incident.title(),
                incident.subtitle(),
                incident.description(),
                incident.latitude(),
                incident.longitude(),
                incident.blocked(),
                incident.future(),
                incident.startTimestamp(),
                incident.riskWeight(),
                incident.source()
        );
    }
}
