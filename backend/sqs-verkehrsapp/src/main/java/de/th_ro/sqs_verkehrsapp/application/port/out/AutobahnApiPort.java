package de.th_ro.sqs_verkehrsapp.application.port.out;

import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;

import java.util.List;

public interface AutobahnApiPort {
    TrafficEventsResult getTrafficEvents(String roadId);
    TrafficEventsResult getAllTrafficEvents();
    List<String> getAvailableRoadIds();
}
