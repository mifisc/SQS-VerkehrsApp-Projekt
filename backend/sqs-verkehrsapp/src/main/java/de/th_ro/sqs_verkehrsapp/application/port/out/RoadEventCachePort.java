package de.th_ro.sqs_verkehrsapp.application.port.out;

import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;

import java.util.List;

public interface RoadEventCachePort {

    void save(String roadId, List<RoadEvent> events);

    TrafficEventsResult findByRoadId(String roadId);
}
