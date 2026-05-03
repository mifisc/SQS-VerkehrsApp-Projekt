package de.th_ro.sqs_verkehrsapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient autobahnWebClient(AutobahnApiProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getBaseUrl())
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)
                )
                .build();
    }
}
