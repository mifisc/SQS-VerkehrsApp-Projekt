package de.th_ro.sqs_verkehrsapp.auth;

import java.time.Instant;

public record AuthResponse(
        String token,
        String username,
        String displayName,
        boolean demoAccount,
        Instant expiresAt
) {
}
