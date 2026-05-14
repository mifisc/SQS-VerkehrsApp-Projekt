package de.th_ro.sqs_verkehrsapp.application.port.in;

import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;

public interface TrafficQueryUseCase {

   TrafficEventsResult getTrafficEvents(String roadId);
}
