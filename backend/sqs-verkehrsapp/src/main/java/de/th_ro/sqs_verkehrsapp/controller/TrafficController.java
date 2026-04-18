package de.th_ro.sqs_verkehrsapp.controller;

import de.th_ro.sqs_verkehrsapp.dto.ClosureDto;
import de.th_ro.sqs_verkehrsapp.dto.RoadworkDto;
import de.th_ro.sqs_verkehrsapp.dto.WarningDto;
import de.th_ro.sqs_verkehrsapp.service.TrafficService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roads")
public class TrafficController {

    private final TrafficService trafficService;

    public TrafficController(TrafficService trafficService) {
        this.trafficService = trafficService;
    }

    @GetMapping("/{roadId}/roadworks")
    public ResponseEntity<List<RoadworkDto>> getRoadworks(@PathVariable String roadId) {
        return ResponseEntity.ok(trafficService.loadRoadworks(roadId));
    }

    @GetMapping("/{roadId}/warnings")
    public ResponseEntity<List<WarningDto>> getWarnings(@PathVariable String roadId) {
        return ResponseEntity.ok(trafficService.loadWarnings(roadId));
    }

    @GetMapping("/{roadId}/closures")
    public ResponseEntity<List<ClosureDto>> getClosures(@PathVariable String roadId) {
        return ResponseEntity.ok(trafficService.loadClosures(roadId));
    }

    @GetMapping("/{roadId}/charging-stations")
    public ResponseEntity<String> getChargingStations(@PathVariable String roadId) {
        return ResponseEntity.ok(trafficService.loadChargingStations(roadId));
    }
}
