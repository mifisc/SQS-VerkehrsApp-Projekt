package de.th_ro.sqs_verkehrsapp.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9._-]{3,30}$", message = "Benutzername muss 3-30 Zeichen lang sein.")
        String username,
        @NotBlank
        @Size(min = 8, max = 72, message = "Passwort muss mindestens 8 Zeichen haben.")
        String password
) {
}
