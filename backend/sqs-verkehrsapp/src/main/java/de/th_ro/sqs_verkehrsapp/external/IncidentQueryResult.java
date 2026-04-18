package de.th_ro.sqs_verkehrsapp.external;

import de.th_ro.sqs_verkehrsapp.incidents.Incident;

import java.time.Instant;
import java.util.List;

public record IncidentQueryResult(
        List<Incident> incidents,
        DataSourceType source,
        Instant generatedAt
) {
}
