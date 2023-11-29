package org.miage.tpae.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long id;

    /**
     * Solde du compte
     */
    private double solde;

    /**
     * Le compte est-il actif ou fermé ?
     */
    private boolean actif = true;

    /**
     * Date de dernière interrogation du compte
     */
    @Temporal(TemporalType.TIMESTAMP)
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
