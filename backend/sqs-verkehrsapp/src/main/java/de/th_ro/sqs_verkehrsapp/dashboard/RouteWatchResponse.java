package de.th_ro.sqs_verkehrsapp.dashboard;

import de.th_ro.sqs_verkehrsapp.external.DataSourceType;

import java.time.Instant;
import java.util.List;

public record RouteWatchResponse(
        Long id,
        String name,
        List<String> roads,
        String notes,
        boolean demoData,
        int riskScore,
        int liveIncidents,
        DataSourceType source,
        Instant refreshedAt,
        List<String> highlights
) {
}
