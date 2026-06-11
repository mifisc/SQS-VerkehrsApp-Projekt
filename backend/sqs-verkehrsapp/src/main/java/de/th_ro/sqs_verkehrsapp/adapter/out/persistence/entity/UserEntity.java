package de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.*;

/**
 * JPA-Entität zur Persistierung von Anwendungsbenutzern.
 * <p>
 * Die Entität speichert die für die Authentifizierung erforderlichen
 * Benutzerdaten. Passwörter werden ausschließlich als Hashwert
 * gespeichert und niemals im Klartext abgelegt.
 */
@Entity
@Table(name = "app_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
}
