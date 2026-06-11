package de.th_ro.sqs_verkehrsapp.adapter.out.persistence.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * JPA-Entität zur Persistierung verfügbarer Autobahnen.
 * <p>
 * Jede Instanz repräsentiert eine Autobahnkennung (z. B. A3, A8),
 * die aus der Autobahn-API geladen und lokal gespeichert wird.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "available_roads")
public class AvailableRoadEntity {

    @Id
    private String roadId;
}
