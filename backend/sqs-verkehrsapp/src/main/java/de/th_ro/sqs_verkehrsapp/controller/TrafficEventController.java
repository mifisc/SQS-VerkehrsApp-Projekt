package de.th_ro.sqs_verkehrsapp.controller;

import de.th_ro.sqs_verkehrsapp.dto.TrafficEventResponse;
import de.th_ro.sqs_verkehrsapp.service.TrafficEventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/traffic-events")
public class TrafficEventController {

    private final TrafficEventService trafficEventService;

    public TrafficEventController(TrafficEventService trafficEventService) {
        this.trafficEventService = trafficEventService;
    }

    @GetMapping("/{roadId}")
    public List<TrafficEventResponse> getTrafficEvents(@PathVariable String roadId) {
        return trafficEventService.getAllTrafficEvents(roadId);
    }
}
