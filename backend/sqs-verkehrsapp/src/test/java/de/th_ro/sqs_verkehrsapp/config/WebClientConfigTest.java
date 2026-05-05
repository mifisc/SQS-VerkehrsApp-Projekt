package de.th_ro.sqs_verkehrsapp.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WebClientConfigTest {

    @Test
    void shouldCreateWebClientBean() {
        AutobahnApiProperties properties = new AutobahnApiProperties();
        properties.setBaseUrl("https://verkehr.autobahn.de/o/autobahn");

        WebClientConfig config = new WebClientConfig();

        WebClient webClient = config.autobahnWebClient(properties);

        assertThat(webClient).isNotNull();
    }
}
