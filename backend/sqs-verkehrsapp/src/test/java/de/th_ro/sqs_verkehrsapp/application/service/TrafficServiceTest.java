package de.th_ro.sqs_verkehrsapp.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrafficServiceTest {

    @Mock
    private AutobahnApiPort autobahnApiPort;

    @InjectMocks
    private TrafficService trafficService;

    @Test
    void shouldReturnAllTrafficEventsInExpectedOrder() {
        String roadId = "A1";

        RoadEvent warning = event("1", roadId, RoadEventType.WARNING);
        RoadEvent roadwork = event("2", roadId, RoadEventType.ROADWORK);
        RoadEvent closure = event("3", roadId, RoadEventType.CLOSURE);

        when(autobahnApiPort.getWarnings(roadId)).thenReturn(List.of(warning));
        when(autobahnApiPort.getRoadworks(roadId)).thenReturn(List.of(roadwork));
        when(autobahnApiPort.getClosures(roadId)).thenReturn(List.of(closure));
        List<RoadEvent> result = trafficService.getTrafficEvents(roadId);

        assertThat(result).containsExactly(warning, roadwork, closure);

        InOrder inOrder = inOrder(autobahnApiPort);
        inOrder.verify(autobahnApiPort).getWarnings(roadId);
        inOrder.verify(autobahnApiPort).getRoadworks(roadId);
        inOrder.verify(autobahnApiPort).getClosures(roadId);
    }

    @Test
    void shouldReturnEmptyListWhenNoEventsExist() {
        String roadId = "A2";

        when(autobahnApiPort.getWarnings(roadId)).thenReturn(List.of());
        when(autobahnApiPort.getRoadworks(roadId)).thenReturn(List.of());
        when(autobahnApiPort.getClosures(roadId)).thenReturn(List.of());

        List<RoadEvent> result = trafficService.getTrafficEvents(roadId);

        assertThat(result).isEmpty();
    }

    private RoadEvent event(String id, String roadId, RoadEventType type) {
        return new RoadEvent(
                id,
                roadId,
                "Title",
                "Subtitle",
                "Description",
                type,
                new Coordinate(50.0, 8.0),
                RiskLevel.MEDIUM
        );
    }
}
