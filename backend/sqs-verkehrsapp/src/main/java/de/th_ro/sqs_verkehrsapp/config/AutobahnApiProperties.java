package de.th_ro.sqs_verkehrsapp.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "autobahn.api")
public class AutobahnApiProperties {

    private String baseUrl;

}
