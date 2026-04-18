package de.th_ro.sqs_verkehrsapp.service;

import de.th_ro.sqs_verkehrsapp.client.AutobahnApiClient;
import de.th_ro.sqs_verkehrsapp.dto.ChargingStationDto;
import de.th_ro.sqs_verkehrsapp.dto.ClosureDto;
import de.th_ro.sqs_verkehrsapp.dto.RoadworkDto;
import de.th_ro.sqs_verkehrsapp.dto.WarningDto;
import de.th_ro.sqs_verkehrsapp.dto.wrapper.ChargingStationResponse;
import de.th_ro.sqs_verkehrsapp.dto.wrapper.ClosureResponse;
import de.th_ro.sqs_verkehrsapp.dto.wrapper.RoadworksResponse;
import de.th_ro.sqs_verkehrsapp.dto.wrapper.WarningResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrafficServiceTest {

    @Mock
    private AutobahnApiClient autobahnApiClient;

    @InjectMocks
    private TrafficService trafficService;

    @Test
    void loadRoadworks() {
        RoadworkDto dto = new RoadworkDto();
        dto.setTitle("A1 | Test Roadwork");

        RoadworksResponse response = new RoadworksResponse();
        response.setRoadworks(List.of(dto));

        when(autobahnApiClient.getRoadworks("A1")).thenReturn(response);

        List<RoadworkDto> result = trafficService.loadRoadworks("A1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("A1 | Test Roadwork");
    }

    @Test
    void loadWarnings() {
        WarningDto dto = new WarningDto();
        dto.setTitle("A1 | Test Warning");

        WarningResponse response = new WarningResponse();
        response.setWarnings(List.of(dto));

        when(autobahnApiClient.getWarnings("A1")).thenReturn(response);

        List<WarningDto> result = trafficService.loadWarnings("A1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("A1 | Test Warning");
    }

    @Test
    void loadClosures() {
        ClosureDto dto = new ClosureDto();
        dto.setTitle("A1 | Test Closure");

        ClosureResponse response = new ClosureResponse();
        response.setClosures(List.of(dto));

        when(autobahnApiClient.getClosures("A1")).thenReturn(response);

        List<ClosureDto> result = trafficService.loadClosures("A1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("A1 | Test Closure");
    }

    @Test
    void loadChargingStations() {
        ChargingStationDto dto = new ChargingStationDto();
        dto.setTitle("A1 | Test Charging Station");

        ChargingStationResponse response = new ChargingStationResponse();
        response.setElectricChargingStations(List.of(dto));

        when(autobahnApiClient.getChargingStations("A1")).thenReturn(response);

        List<ChargingStationDto> result = trafficService.loadChargingStations("A1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("A1 | Test Charging Station");
    }
}