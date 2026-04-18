package de.th_ro.sqs_verkehrsapp.incidents;

import de.th_ro.sqs_verkehrsapp.external.AutobahnIncidentGateway;
import de.th_ro.sqs_verkehrsapp.external.IncidentQueryResult;
import de.th_ro.sqs_verkehrsapp.shared.RoadNormalizer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {
    private static final List<String> DEFAULT_ROADS = List.of("A1", "A3", "A8");

    private final AutobahnIncidentGateway incidentGateway;

    public IncidentService(AutobahnIncidentGateway incidentGateway) {
        this.incidentGateway = incidentGateway;
    }

    public IncidentResponse loadPublicIncidents(List<String> roads, boolean all, Integer limit) {
        List<String> normalizedRoads = all ? loadAvailableRoutes() : resolveRoads(roads);
        IncidentQueryResult result = incidentGateway.loadIncidents(normalizedRoads);
        var sortedStream = result.incidents()
                .stream()
                .sorted(Incident.SORT_ORDER);
        List<Incident> sortedIncidents = limit == null
                ? sortedStream.toList()
                : sortedStream.limit(Math.max(1, limit)).toList();

        return new IncidentResponse(
                normalizedRoads,
                sortedIncidents.stream().map(IncidentDto::from).toList(),
                buildStats(sortedIncidents),
                result.source(),
                result.generatedAt()
        );
    }

    public RouteRiskSnapshot buildRouteRisk(List<String> roads) {
        List<String> normalizedRoads = resolveRoads(roads);
        IncidentQueryResult result = incidentGateway.loadIncidents(normalizedRoads);
        List<Incident> sortedIncidents = result.incidents()
                .stream()
                .sorted(Incident.SORT_ORDER)
                .toList();

        int riskScore = calculateRiskScore(sortedIncidents);
        List<String> highlights = sortedIncidents.stream()
                .limit(3)
                .map(incident -> incident.category().label() + ": " + incident.title())
                .toList();

        return new RouteRiskSnapshot(
                normalizedRoads,
                riskScore,
                sortedIncidents.size(),
                result.source(),
                result.generatedAt(),
                highlights
        );
    }

    public List<String> resolveRoads(List<String> roads) {
        List<String> normalizedRoads = RoadNormalizer.normalize(roads);
        return normalizedRoads.isEmpty() ? DEFAULT_ROADS : normalizedRoads;
    }

    public List<String> loadAvailableRoutes() {
        return incidentGateway.loadAvailableRoads();
    }

    private IncidentStatsResponse buildStats(List<Incident> incidents) {
        int warnings = (int) incidents.stream().filter(incident -> incident.category() == IncidentCategory.WARNING).count();
        int roadworks = (int) incidents.stream().filter(incident -> incident.category() == IncidentCategory.ROADWORK).count();
        int closures = (int) incidents.stream().filter(incident -> incident.category() == IncidentCategory.CLOSURE).count();
        int blocked = (int) incidents.stream().filter(Incident::blocked).count();
        int riskScore = calculateRiskScore(incidents);

        return new IncidentStatsResponse(incidents.size(), warnings, roadworks, closures, blocked, riskScore);
    }

    private int calculateRiskScore(List<Incident> incidents) {
        if (incidents.isEmpty()) {
            return 0;
        }

        double averageSeverity = incidents.stream()
                .mapToInt(Incident::riskWeight)
                .average()
                .orElse(0);
        int blockedCount = (int) incidents.stream().filter(Incident::blocked).count();
        int closureCount = (int) incidents.stream().filter(incident -> incident.category() == IncidentCategory.CLOSURE).count();
        int densityScore = Math.min(36, incidents.size() * 6);
        int blockedBonus = Math.min(24, blockedCount * 12);
        int closureBonus = Math.min(20, closureCount * 8);

        return Math.min(100, (int) Math.round(averageSeverity * 1.4 + densityScore + blockedBonus + closureBonus));
    }
}
