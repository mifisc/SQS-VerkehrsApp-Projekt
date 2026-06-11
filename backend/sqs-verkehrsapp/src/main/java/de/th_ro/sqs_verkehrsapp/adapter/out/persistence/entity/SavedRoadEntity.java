package de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity;


import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;

/**
 * JPA-Entität für vom Benutzer gespeicherte Autobahnen.
 * <p>
 * Jede Instanz verknüpft einen Benutzer mit einer Autobahn, die er
 * als Favorit gespeichert hat. Durch den Unique Constraint kann eine
 * Autobahn pro Benutzer nur einmal gespeichert werden.
 */
@Entity
@Table(
        name = "saved_roads",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "road_id"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedRoadEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "road_id", nullable = false)
    private String roadId;
}
