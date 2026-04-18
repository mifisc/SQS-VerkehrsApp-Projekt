package de.th_ro.sqs_verkehrsapp.auth;

import de.th_ro.sqs_verkehrsapp.user.AppUser;
import de.th_ro.sqs_verkehrsapp.user.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Locale;

@Service
public class AuthService {
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = normalizeUsername(request.username());
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Benutzername ist bereits vergeben.");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setDisplayName(resolveDisplayName(request.displayName(), username));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDemoAccount(false);

        AppUser savedUser = userRepository.save(user);
        return buildResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        String username = normalizeUsername(request.username());
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ungültige Zugangsdaten."));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ungültige Zugangsdaten.");
        }

        return buildResponse(user);
    }

    private AuthResponse buildResponse(AppUser user) {
        String token = jwtService.generateToken(user);
        Instant expiresAt = jwtService.extractExpiration(token);

        return new AuthResponse(
                token,
                user.getUsername(),
                user.getDisplayName(),
                user.isDemoAccount(),
                expiresAt
        );
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }

    private String resolveDisplayName(String displayName, String username) {
        if (displayName == null || displayName.isBlank()) {
            return username;
        }
        return displayName.trim();
    }
}
