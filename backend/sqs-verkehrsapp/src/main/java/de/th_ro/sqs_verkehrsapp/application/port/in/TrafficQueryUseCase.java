package de.th_ro.sqs_verkehrsapp.application.port.in;

import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;

import java.util.List;

public interface TrafficQueryUseCase {

    List<RoadEvent> getTrafficEvents(String roadId);
}
