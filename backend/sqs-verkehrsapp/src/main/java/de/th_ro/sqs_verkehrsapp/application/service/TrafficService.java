package de.th_ro.sqs_verkehrsapp.application.service;

import de.th_ro.sqs_verkehrsapp.application.port.in.TrafficQueryUseCase;
import de.th_ro.sqs_verkehrsapp.application.port.out.AutobahnApiPort;
import de.th_ro.sqs_verkehrsapp.domain.logic.RiskScoreCalculator;
import de.th_ro.sqs_verkehrsapp.domain.model.TrafficEventsResult;
import org.springframework.stereotype.Service;

@Service
public class TrafficService implements TrafficQueryUseCase {

    private final AutobahnApiPort autobahnApiPort;
    private final RiskScoreCalculator riskScoreCalculator;

    public TrafficService(AutobahnApiPort autobahnApiPort) {
        this.autobahnApiPort = autobahnApiPort;
        this.riskScoreCalculator = new RiskScoreCalculator();
    }

    @Override
    public TrafficEventsResult getTrafficEvents(String roadId) {
        TrafficEventsResult result = autobahnApiPort.getTrafficEvents(roadId);

        int riskScore = riskScoreCalculator.calculateRiskScore(result.events());

        return new TrafficEventsResult(
                result.events(),
                result.live(),
                result.cachedAt(),
                riskScore
        );
    }

    @Override
    public TrafficEventsResult getAllTrafficEvents() {

        TrafficEventsResult result = autobahnApiPort.getAllTrafficEvents();

        int riskScore = riskScoreCalculator.calculateRiskScore(result.events());

        return new TrafficEventsResult(
                result.events(),
                result.live(),
                result.cachedAt(),
                riskScore
        );
    }
}
