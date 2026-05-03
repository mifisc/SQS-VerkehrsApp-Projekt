package de.th_ro.sqs_verkehrsapp.application.port.out;

import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;

import java.util.List;

public interface AutobahnApiPort {
    List<RoadEvent> getTrafficEvents(String roadId);
}
