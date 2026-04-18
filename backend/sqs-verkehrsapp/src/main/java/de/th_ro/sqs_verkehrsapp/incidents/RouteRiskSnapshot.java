package de.th_ro.sqs_verkehrsapp.incidents;

import de.th_ro.sqs_verkehrsapp.external.DataSourceType;

import java.time.Instant;
import java.util.List;

public record RouteRiskSnapshot(
        List<String> roads,
        int riskScore,
        int liveIncidents,
        DataSourceType source,
        Instant refreshedAt,
        List<String> highlights
) {
}
