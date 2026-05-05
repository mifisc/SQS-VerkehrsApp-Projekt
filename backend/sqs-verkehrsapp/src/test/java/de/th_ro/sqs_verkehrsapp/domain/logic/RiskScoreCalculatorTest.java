package de.th_ro.sqs_verkehrsapp.domain.logic;

import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RiskScoreCalculatorTest {

    private final RiskScoreCalculator riskScoreCalculator = new RiskScoreCalculator();

    @Test
    void shouldReturnHighRiskForClosure() {
        assertThat(riskScoreCalculator.calculateRiskLevel(RoadEventType.CLOSURE))
                .isEqualTo(RiskLevel.HIGH);
    }

    @Test
    void shouldReturnMediumRiskForWarning() {
        assertThat(riskScoreCalculator.calculateRiskLevel(RoadEventType.WARNING))
                .isEqualTo(RiskLevel.MEDIUM);
    }

    @Test
    void shouldReturnMediumRiskForRoadwork() {
        assertThat(riskScoreCalculator.calculateRiskLevel(RoadEventType.ROADWORK))
                .isEqualTo(RiskLevel.MEDIUM);
    }

    @Test
    void shouldReturnLowRiskForChargingStation() {
        assertThat(riskScoreCalculator.calculateRiskLevel(RoadEventType.CHARGING_STATION))
                .isEqualTo(RiskLevel.LOW);
    }
}
