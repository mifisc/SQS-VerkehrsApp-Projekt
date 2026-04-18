package de.th_ro.sqs_verkehrsapp.dashboard;

import java.util.List;

public record DashboardResponse(
        String username,
        String displayName,
        boolean demoAccount,
        List<RouteWatchResponse> routeWatches
) {
}
