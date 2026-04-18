package de.th_ro.sqs_verkehrsapp.external;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.external.autobahn")
public record AutobahnApiProperties(
        @NotBlank String baseUrl,
        @NotNull Duration requestTimeout,
        @PositiveOrZero int maxRetries,
        @NotNull Duration cacheTtl
) {
}
