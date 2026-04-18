package de.th_ro.sqs_verkehrsapp.incidents;

public record IncidentStatsResponse(
        int total,
        int warnings,
        int roadworks,
        int closures,
        int blocked,
        int riskScore
) {
}
