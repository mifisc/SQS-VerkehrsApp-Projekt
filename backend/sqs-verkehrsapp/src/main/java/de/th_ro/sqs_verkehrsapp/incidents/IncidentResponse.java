package de.th_ro.sqs_verkehrsapp.incidents;

import de.th_ro.sqs_verkehrsapp.external.DataSourceType;

import java.time.Instant;
import java.util.List;

public record IncidentResponse(
        List<String> roads,
        List<IncidentDto> incidents,
        IncidentStatsResponse stats,
        DataSourceType source,
        Instant generatedAt
) {
}
