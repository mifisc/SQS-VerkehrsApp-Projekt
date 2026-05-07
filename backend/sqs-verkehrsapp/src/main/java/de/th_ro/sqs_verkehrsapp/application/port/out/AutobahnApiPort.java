package de.th_ro.sqs_verkehrsapp.application.port.out;

import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import java.util.List;

public interface AutobahnApiPort {
    List<RoadEvent> getWarnings(String roadId);
    List<RoadEvent> getRoadworks(String roadId);
    List<RoadEvent> getClosures(String roadId);
}
