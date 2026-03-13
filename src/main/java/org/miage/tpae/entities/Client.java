package org.miage.tpae.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entités représentant les Clients de la Banque
 */
@Entity
@Data
@NoArgsConstructor
public class Client {
    /**
     * Id de l'entité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Id du client", example = "1")
    private Long id;

    /**
     * Nom du client
     */
    @NotNull
    @Schema(description = "Nom d'un client", example = "Dupont")
    private String nom;

    /**
     * Prénom du client
     */
    @NotNull
    @Schema(description = "Prénom d'un client", example = "Jean")
    private String prenom;

    /**
     * Liste des comptes du client
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "client")
    @JsonManagedReference // on inclura les comptes en JSON
    @Schema(description = "Liste des comptes d'un client", example = "")
    private List<Compte> comptes;

    /**
     * Méthode pour afficher un client
     * @return une représentation textuelle du client
     */
    @Override
    public String toString() {
        // attention aux cycles
        return "Client{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", comptes=" + comptes +
                '}';
    }
}
