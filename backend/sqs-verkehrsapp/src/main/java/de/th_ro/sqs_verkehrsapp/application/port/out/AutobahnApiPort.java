package de.th_ro.sqs_verkehrsapp.application.port.out;

import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;

public interface AutobahnApiPort {
    TrafficEventsResult getTrafficEvents(String roadId);
}
