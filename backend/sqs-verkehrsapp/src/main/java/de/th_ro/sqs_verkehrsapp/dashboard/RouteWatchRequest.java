package de.th_ro.sqs_verkehrsapp.dashboard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RouteWatchRequest(
        @NotBlank
        @Size(max = 80, message = "Name darf maximal 80 Zeichen lang sein.")
        String name,
        @NotEmpty(message = "Mindestens eine Straße ist erforderlich.")
        List<String> roads,
        @Size(max = 255, message = "Notizen dürfen maximal 255 Zeichen lang sein.")
        String notes
) {
}
