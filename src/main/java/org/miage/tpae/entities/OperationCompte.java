package org.miage.tpae.entities;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Id de l'opération", example = "1")
    private Long id;

    /**
     * Type de l'opération
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Schema(description = "Type de l'opération", example = "CREDIT")
    private OperationType operationType;

    /**
     * Valeur de l'opération
     */
    @NotNull
    @Schema(description = "Valeur de l'opération", example = "1000")
    private double valeur;

    /**
     * Date de l'opération
     */
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Schema(description = "Date de l'opération", example = "2026-03-13T08:40:40.539Z")
    private Calendar dateOperation;

    /**
     * Compte sur lequel l'opération a eu lieu
     */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn
    @Schema(description = "Compte sur lequel l'opération a eu lieu", example = "")
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
