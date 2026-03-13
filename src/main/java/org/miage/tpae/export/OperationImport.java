package org.miage.tpae.export;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Valeur de l'opération", example = "100")
    private double valeur;
    /**
     * Type d'opération : ici seules CREDIT et DEBIT sont utiles
     */
    @Schema(description = "Type de l'opération", example = "CREDIT")
    private OperationCompte.OperationType operationType;
}
