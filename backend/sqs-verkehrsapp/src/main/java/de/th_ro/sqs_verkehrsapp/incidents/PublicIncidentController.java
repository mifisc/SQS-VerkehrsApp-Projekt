package de.th_ro.sqs_verkehrsapp.incidents;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicIncidentController {
    private final IncidentService incidentService;

    public PublicIncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping("/incidents")
    public IncidentResponse incidents(
            @RequestParam(required = false) String roads,
            @RequestParam(defaultValue = "false") boolean all,
            @RequestParam(required = false) Integer limit
    ) {
        return incidentService.loadPublicIncidents(splitRoads(roads), all, limit);
    }

    @GetMapping("/routes")
    public List<String> routes() {
        return incidentService.loadAvailableRoutes();
    }

    private List<String> splitRoads(String roads) {
        if (roads == null || roads.isBlank()) {
            return List.of();
        }
        return Arrays.stream(roads.split(","))
                .map(String::trim)
                .toList();
    }
}
