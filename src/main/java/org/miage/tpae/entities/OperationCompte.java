package org.miage.tpae.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Entité représentant les opérations sur un compte
 */
@Entity
@Data
@NoArgsConstructor
public class OperationCompte {


    /**
     * Enum des types d'opérations
     */
    public enum OperationType {OUVERTURE, CLOTURE, DEBIT, CREDIT, VIREMENT_CREDIT, VIREMENT_DEBIT, CONSULTATION}

    /**
     * Id de l'entité
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Type de l'opération
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private OperationType operationType;

    /**
     * Valeur de l'opération
     */
    @NotNull
    private double valeur;

    /**
     * Date de l'opération
     */
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Calendar dateOperation;

    /**
     * Compte sur lequel l'opération a eu lieu
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn
    private Compte compte;

    /**
     * Constructeur
     * @param compte le compte
     * @param operationType le type d'opération
     * @param valeur la valeur (somme)
     */
    public OperationCompte(Compte compte, OperationType operationType, double valeur) {
        this.compte = compte;
        this.operationType = operationType;
        this.valeur = valeur;
        this.dateOperation = GregorianCalendar.getInstance();
    }

    /**
     * Méthode pour afficher l'opération
     * @return une représentation textuelle de l'opération
     */
    @Override
    public String toString() {
        // attention aux cycles
        return "OperationCompte{" +
                "id=" + id +
                ", operationType=" + operationType +
                ", valeur=" + valeur +
                ", dateOperation=" + dateOperation +
                ", compte=" + compte +
                '}';
    }
}
