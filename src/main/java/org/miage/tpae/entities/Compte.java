package org.miage.tpae.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.List;

/**
 * Entités représentant les Comptes de la Banque
 */
@Entity
@Data
@NoArgsConstructor
public class Compte {
    /**
     * Id de l'entité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(description = "Id d'un compte", example = "1")
    private Long id;

    /**
     * Solde du compte
     */
    @Schema(description = "Solde d'un compte", example = "1000")
    private double solde;

    /**
     * Le compte est-il actif ou fermé ?
     */
    @Schema(description = "Le compte est-il actif ou fermé ?", example = "true")
    private boolean actif = true;

    /**
     * Date de dernière interrogation du compte
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(description = "Date de dernière interrogation du compte", example = "2026-03-13T08:38:20.296Z")
    private Calendar dateInterrogation;

    /**
     * Référence vers le client qui possède ce compte
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonBackReference // pour éviter les cycles lors de la transformation en JSON
    private Client client;

    /**
     * Liste des opérations sur ce compte
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "compte")
    @JsonIgnore // pour éviter les cycles lors de la transformation en JSON
    private List<OperationCompte> operations;

    /**
     * Méthode pour afficher le compte
     * @return une représentation textuelle
     */
    @Override
    public String toString() {
        // attention aux cycles
        // Ici on choisit de ne pas afficher ni le client ni la liste d'opérations
        return "Compte{" +
                "id=" + id +
                ", solde=" + solde +
                ", actif=" + actif +
                ", dateInterrogation=" + dateInterrogation +
                '}';
    }
}
