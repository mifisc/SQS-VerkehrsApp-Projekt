package de.th_ro.sqs_verkehrsapp.application.port.out;

import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;

import java.util.List;

public interface RoadEventCachePort {

    void save(String roadId, List<RoadEvent> events);

    List<RoadEvent> findByRoadId(String roadId);
}
