package de.th_ro.sqs_verkehrsapp.dashboard;

import de.th_ro.sqs_verkehrsapp.incidents.IncidentService;
import de.th_ro.sqs_verkehrsapp.incidents.RouteRiskSnapshot;
import de.th_ro.sqs_verkehrsapp.shared.RoadNormalizer;
import de.th_ro.sqs_verkehrsapp.user.AppUser;
import de.th_ro.sqs_verkehrsapp.user.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;

@Service
public class DashboardService {
    private final AppUserRepository userRepository;
    private final RouteWatchRepository routeWatchRepository;
    private final IncidentService incidentService;

    public DashboardService(
            AppUserRepository userRepository,
            RouteWatchRepository routeWatchRepository,
            IncidentService incidentService
    ) {
        this.userRepository = userRepository;
        this.routeWatchRepository = routeWatchRepository;
        this.incidentService = incidentService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse loadDashboard(String username) {
        AppUser user = findUser(username);
        List<RouteWatchResponse> routeWatches = routeWatchRepository.findAllByUserOrderByCreatedAtAsc(user)
                .stream()
                .map(this::toResponse)
                .toList();

        return new DashboardResponse(
                user.getUsername(),
                user.getDisplayName(),
                user.isDemoAccount(),
                routeWatches
        );
    }

    @Transactional
    public RouteWatchResponse addRouteWatch(String username, RouteWatchRequest request) {
        AppUser user = findUser(username);
        List<String> roads = RoadNormalizer.normalize(request.roads());
        if (roads.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bitte mindestens eine gültige Straße angeben.");
        }

        RouteWatch routeWatch = new RouteWatch();
        routeWatch.setUser(user);
        routeWatch.setName(request.name().trim());
        routeWatch.setRoadIds(String.join(",", roads));
        routeWatch.setNotes(request.notes() == null || request.notes().isBlank() ? null : request.notes().trim());
        routeWatch.setDemoData(false);

        RouteWatch savedRouteWatch = routeWatchRepository.save(routeWatch);
        return toResponse(savedRouteWatch);
    }

    @Transactional
    public void deleteRouteWatch(String username, Long routeWatchId) {
        AppUser user = findUser(username);
        RouteWatch routeWatch = routeWatchRepository.findByIdAndUser(routeWatchId, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Route wurde nicht gefunden."));
        routeWatchRepository.delete(routeWatch);
    }

    private RouteWatchResponse toResponse(RouteWatch routeWatch) {
        List<String> roads = Arrays.stream(routeWatch.getRoadIds().split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toList();
        RouteRiskSnapshot snapshot = incidentService.buildRouteRisk(roads);

        return new RouteWatchResponse(
                routeWatch.getId(),
                routeWatch.getName(),
                snapshot.roads(),
                routeWatch.getNotes(),
                routeWatch.isDemoData(),
                snapshot.riskScore(),
                snapshot.liveIncidents(),
                snapshot.source(),
                snapshot.refreshedAt(),
                snapshot.highlights()
        );
    }

    private AppUser findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Benutzer wurde nicht gefunden."));
    }
}
