package de.th_ro.sqs_verkehrsapp.application.service;


import de.th_ro.sqs_verkehrsapp.application.port.in.AuthUseCase;
import de.th_ro.sqs_verkehrsapp.domain.model.AppUser;
import de.th_ro.sqs_verkehrsapp.domain.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser register(String username, String password) {

        if (userRepositoryPort.existsByUsername(username)) {
            throw new IllegalArgumentException("Username ist bereits vergeben");
        }

        AppUser user = AppUser.builder()
                .id(UUID.randomUUID())
                .username(username)
                .passwordHash(passwordEncoder.encode(password))
                .build();

        return userRepositoryPort.save(user);
    }

    @Override
    public AppUser login(String username, String password) {

        AppUser user = userRepositoryPort.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Ungültige Login-Daten"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Ungültige Login-Daten");
        }

        return user;
    }
}
