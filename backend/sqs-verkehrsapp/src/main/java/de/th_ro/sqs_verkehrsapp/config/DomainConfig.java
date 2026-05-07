package de.th_ro.sqs_verkehrsapp.config;

import de.th_ro.sqs_verkehrsapp.domain.logic.RiskScoreCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public RiskScoreCalculator riskScoreCalculator() {
        return new RiskScoreCalculator();
    }
}
