package de.th_ro.sqs_verkehrsapp.domain.port.out;

import de.th_ro.sqs_verkehrsapp.domain.model.AppUser;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    AppUser save(AppUser user);

    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findById(UUID id);

    boolean existsByUsername(String username);
}
