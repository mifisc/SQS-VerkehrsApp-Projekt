package de.th_ro.sqs_verkehrsapp.application.port.in;

import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;

import java.util.List;

public interface TrafficQueryUseCase {

   TrafficEventsResult getTrafficEvents(String roadId);

    TrafficEventsResult getAllTrafficEvents();

    List<String> getAvailableRoadIds();
}
