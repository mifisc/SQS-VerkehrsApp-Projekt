package de.th_ro.sqs_verkehrsapp.application.service;

import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;
import org.springframework.stereotype.Service;

@Service
public class TrafficService implements TrafficQueryUseCase {

    private final AutobahnApiPort autobahnApiPort;

    public TrafficService(AutobahnApiPort autobahnApiPort) {
        this.autobahnApiPort = autobahnApiPort;
    }

    @Override
    public TrafficEventsResult getTrafficEvents(String roadId) {
        return autobahnApiPort.getTrafficEvents(roadId);
    }
}
