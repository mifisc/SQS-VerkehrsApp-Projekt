package de.th_ro.sqs_verkehrsapp.adapter.out.autobahn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import de.th_ro.sqs_verkehrsapp.application.port.out.RoadEventCachePort;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResilientAutobahnApiAdapterTest {

    @Mock
    private AutobahnApiClient autobahnApiClient;

    @Mock
    private RoadEventCachePort cachePort;

    @InjectMocks
    private ResilientAutobahnApiAdapter adapter;

    @Test
    void getTrafficEvents_shouldFetchEventsAndSaveThemInCache() {
        String roadId = "A1";
        RoadEvent event = mock(RoadEvent.class);
        List<RoadEvent> events = List.of(event);

        when(autobahnApiClient.fetchTrafficEvents(roadId)).thenReturn(events);

        List<RoadEvent> result = adapter.getTrafficEvents(roadId);

        assertThat(result).isEqualTo(events);

        verify(autobahnApiClient).fetchTrafficEvents(roadId);
        verify(cachePort).save(roadId, events);
        verify(cachePort, never()).findByRoadId(anyString());
    }

    @Test
    void getTrafficEventsFallback_shouldReturnCachedEvents() {
        String roadId = "A1";
        Throwable throwable = new RuntimeException("API unavailable");

        RoadEvent cachedEvent = mock(RoadEvent.class);
        List<RoadEvent> cachedEvents = List.of(cachedEvent);

        when(cachePort.findByRoadId(roadId)).thenReturn(cachedEvents);

        List<RoadEvent> result = adapter.getTrafficEventsFallback(roadId, throwable);

        assertThat(result).isEqualTo(cachedEvents);

        verify(cachePort).findByRoadId(roadId);
        verifyNoInteractions(autobahnApiClient);
    }
}
