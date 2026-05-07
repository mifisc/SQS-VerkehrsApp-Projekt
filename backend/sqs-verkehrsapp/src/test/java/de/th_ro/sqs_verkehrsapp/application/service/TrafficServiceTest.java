package de.th_ro.sqs_verkehrsapp.application.service;

import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        RoadEvent charging = event("4", roadId, RoadEventType.CHARGING_STATION);

        List<RoadEvent> expectedEvents = List.of(
                warning,
                roadwork,
                closure,
                charging
        );

        when(autobahnApiPort.getTrafficEvents(roadId))
                .thenReturn(expectedEvents);

        List<RoadEvent> result = trafficService.getTrafficEvents(roadId);

        assertThat(result).containsExactlyElementsOf(expectedEvents);

        verify(autobahnApiPort).getTrafficEvents(roadId);
    }

    @Test
    void shouldReturnEmptyListWhenNoEventsExist() {
        String roadId = "A2";

        when(autobahnApiPort.getTrafficEvents(roadId))
                .thenReturn(List.of());

        List<RoadEvent> result = trafficService.getTrafficEvents(roadId);

        assertThat(result).isEmpty();

        verify(autobahnApiPort).getTrafficEvents(roadId);
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
