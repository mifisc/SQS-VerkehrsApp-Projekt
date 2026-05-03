package de.th_ro.sqs_verkehrsapp.domain.logic;

import de.th_ro.sqs_verkehrsapp.domain.model.RiskLevel;
import de.th_ro.sqs_verkehrsapp.domain.model.RoadEventType;
import org.springframework.stereotype.Component;

@Component
public class RiskScoreCalculator {

    public RiskLevel calculateRiskLevel(RoadEventType type) {
        return switch (type) {
            case CLOSURE -> RiskLevel.HIGH;
            case WARNING, ROADWORK -> RiskLevel.MEDIUM;
            case CHARGING_STATION -> RiskLevel.LOW;
        };
    }
}
