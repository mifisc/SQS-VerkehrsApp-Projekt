package de.th_ro.sqs_verkehrsapp.adapter.out.persistence;

import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity.UserEntity;
import de.th_ro.sqs_verkehrsapp.adapter.out.persistence.repository.UserRepository;
import de.th_ro.sqs_verkehrsapp.application.port.out.UserPort;
import de.th_ro.sqs_verkehrsapp.domain.model.AppUser;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Persistence-Adapter für Anwendungsbenutzer.
 * <p>
 * Implementiert {@link UserPort} mittels Spring Data JPA und übernimmt
 * das Mapping zwischen Domain-Modellen und Persistenz-Entitäten.
 */
@Component
@RequiredArgsConstructor
public class UserAdapter implements UserPort {

    private final UserRepository repository;

    @Override
    public AppUser save(AppUser user) {

        UserEntity entity = UserEntity.builder()
                .id(user.getId())
                .username(user.getUsername())
                .passwordHash(user.getPasswordHash())
                .build();

        return mapToDomain(repository.save(entity));
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return repository.findByUsername(username)
                .map(this::mapToDomain);
    }

    @Override
    public Optional<AppUser> findById(UUID id) {
        return repository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    /**
     * Wandelt eine Benutzer-Entität in das entsprechende Domain-Modell um.
     *
     * @param entity Persistenz-Entität
     * @return Domain-Modell
     */
    private AppUser mapToDomain(UserEntity entity) {
        return AppUser.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .passwordHash(entity.getPasswordHash())
                .build();
    }
}
