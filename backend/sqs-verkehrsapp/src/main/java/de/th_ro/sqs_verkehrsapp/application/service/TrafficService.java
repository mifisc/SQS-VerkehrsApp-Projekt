package de.th_ro.sqs_verkehrsapp.application.service;

import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TrafficService implements TrafficQueryUseCase {

    private final AutobahnApiPort autobahnApiPort;

    public TrafficService(AutobahnApiPort autobahnApiPort) {
        this.autobahnApiPort = autobahnApiPort;
    }

    @Override
    public List<RoadEvent> getTrafficEvents(String roadId) {
        List<RoadEvent> events = new ArrayList<>();

        events.addAll(autobahnApiPort.getWarnings(roadId));
        events.addAll(autobahnApiPort.getRoadworks(roadId));
        events.addAll(autobahnApiPort.getClosures(roadId));

        return events;
    }
}
