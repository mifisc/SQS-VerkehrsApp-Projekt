package de.th_ro.sqs_verkehrsapp.adapter.in.web;

import de.th_ro.sqs_verkehrsapp.adapter.in.web.dto.TrafficResponseDto;
import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/traffic")
public class TrafficController {

    private final TrafficQueryUseCase trafficQueryUseCase;

    public TrafficController(TrafficQueryUseCase trafficQueryUseCase) {
        this.trafficQueryUseCase = trafficQueryUseCase;
    }

    @GetMapping("/{roadId}")
    public List<TrafficResponseDto> getTrafficEvents(@PathVariable String roadId) {
        return trafficQueryUseCase.getTrafficEvents(roadId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    private TrafficResponseDto toResponseDto(RoadEvent event) {
        return new TrafficResponseDto(
                event.id(),
                event.roadId(),
                event.title(),
                event.subtitle(),
                event.description(),
                event.type(),
                event.coordinate().latitude(),
                event.coordinate().longitude(),
                event.riskLevel()
        );
    }
}
