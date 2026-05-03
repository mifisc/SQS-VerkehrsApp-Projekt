package de.th_ro.sqs_verkehrsapp.application.service;

import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrafficService implements TrafficQueryUseCase {

    private final AutobahnApiPort autobahnApiPort;

    public TrafficService(AutobahnApiPort autobahnApiPort) {
        this.autobahnApiPort = autobahnApiPort;
    }

    @Override
    public List<RoadEvent> getTrafficEvents(String roadId) {
        return autobahnApiPort.getTrafficEvents(roadId);
    }
}
