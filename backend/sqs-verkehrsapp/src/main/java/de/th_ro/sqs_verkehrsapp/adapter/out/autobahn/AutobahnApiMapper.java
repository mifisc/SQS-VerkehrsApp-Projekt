package de.th_ro.sqs_verkehrsapp.adapter.out.autobahn;

import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.BaseAutobahnDto;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.CoordinateDto;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.ChargingStationResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.ClosureResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.RoadworksResponse;
import de.th_ro.sqs_verkehrsapp.adapter.out.autobahn.dto.wrapper.WarningResponse;
import de.th_ro.sqs_verkehrsapp.domain.logic.RiskScoreCalculator;
import de.th_ro.sqs_verkehrsapp.domain.model.Coordinate;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEvent;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AutobahnApiMapper {

    private final RiskScoreCalculator riskScoreCalculator;

    public AutobahnApiMapper(RiskScoreCalculator riskScoreCalculator) {
        this.riskScoreCalculator = riskScoreCalculator;
    }

    public List<RoadEvent> mapWarnings(String roadId, WarningResponse response) {
        if (response == null || response.getWarnings() == null) {
            return Collections.emptyList();
        }

        return response.getWarnings()
                .stream()
                .map(dto -> map(dto, roadId, RoadEventType.WARNING))
                .toList();
    }

    public List<RoadEvent> mapRoadworks(String roadId, RoadworksResponse response) {
        if (response == null || response.getRoadworks() == null) {
            return Collections.emptyList();
        }

        return response.getRoadworks()
                .stream()
                .map(dto -> map(dto, roadId, RoadEventType.ROADWORK))
                .toList();
    }

    public List<RoadEvent> mapClosures(String roadId, ClosureResponse response) {
        if (response == null || response.getClosures() == null) {
            return Collections.emptyList();
        }

        return response.getClosures()
                .stream()
                .map(dto -> map(dto, roadId, RoadEventType.CLOSURE))
                .toList();
    }

    public List<RoadEvent> mapChargingStations(String roadId, ChargingStationResponse response) {
        if (response == null || response.getElectricChargingStations() == null) {
            return Collections.emptyList();
        }

        return response.getElectricChargingStations()
                .stream()
                .map(dto -> map(dto, roadId, RoadEventType.CHARGING_STATION))
                .toList();
    }

    private RoadEvent map(BaseAutobahnDto dto, String roadId, RoadEventType type) {
        return new RoadEvent(
                dto.getIdentifier(),
                roadId,
                dto.getTitle(),
                dto.getSubtitle(),
                String.join("\n", dto.getDescription() == null ? List.of() : dto.getDescription()),
                type,
                mapCoordinate(dto.getCoordinate()),
                riskScoreCalculator.calculateRiskLevel(type)
        );
    }

    private Coordinate mapCoordinate(CoordinateDto dto) {
        if (dto == null) {
            return new Coordinate(0.0, 0.0);
        }

        return new Coordinate(
                Double.parseDouble(dto.getLat()),
                Double.parseDouble(dto.getLongValue())
        );
    }
}
