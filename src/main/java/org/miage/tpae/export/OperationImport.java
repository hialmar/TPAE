package org.miage.tpae.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.miage.tpae.entities.OperationCompte;

/**
 * Permet d'envoyer le détail d'une opération à réaliser
 */
@Data
@AllArgsConstructor
public class OperationImport {
    /**
     * Valeur (somme) de l'opération
     */
    private double valeur;
    /**
     * Type d'opération : ici seules CREDIT et DEBIT sont utiles
     */
    private OperationCompte.OperationType operationType;
}
