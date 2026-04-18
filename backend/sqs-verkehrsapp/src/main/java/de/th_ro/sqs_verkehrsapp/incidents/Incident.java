package de.th_ro.sqs_verkehrsapp.incidents;

import de.th_ro.sqs_verkehrsapp.external.DataSourceType;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public record Incident(
        String id,
        String roadId,
        IncidentCategory category,
        String title,
        String subtitle,
        List<String> description,
        double latitude,
        double longitude,
        boolean blocked,
        boolean future,
        Instant startTimestamp,
        DataSourceType source
) {
    public static final Comparator<Incident> SORT_ORDER = Comparator
            .comparing(Incident::blocked).reversed()
            .thenComparing((Incident incident) -> incident.category().baseRisk()).reversed()
            .thenComparing(Incident::startTimestamp, Comparator.nullsLast(Comparator.reverseOrder()))
            .thenComparing(Incident::title);

    public Incident withSource(DataSourceType newSource) {
        return new Incident(
                id,
                roadId,
                category,
                title,
                subtitle,
                description,
                latitude,
                longitude,
                blocked,
                future,
                startTimestamp,
                newSource
        );
    }

    public int riskWeight() {
        int score = category.baseRisk();
        if (blocked) {
            score += 18;
        }
        if (future) {
            score = Math.max(6, score - 4);
        }
        return score;
    }
}
