package de.th_ro.sqs_verkehrsapp.dashboard;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public DashboardResponse dashboard(Authentication authentication) {
        return dashboardService.loadDashboard(authentication.getName());
    }

    @PostMapping("/routes")
    @ResponseStatus(HttpStatus.CREATED)
    public RouteWatchResponse addRoute(Authentication authentication, @Valid @RequestBody RouteWatchRequest request) {
        return dashboardService.addRouteWatch(authentication.getName(), request);
    }

    @DeleteMapping("/routes/{routeWatchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRoute(Authentication authentication, @PathVariable Long routeWatchId) {
        dashboardService.deleteRouteWatch(authentication.getName(), routeWatchId);
    }
}
