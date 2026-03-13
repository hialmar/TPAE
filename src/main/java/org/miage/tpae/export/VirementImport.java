package org.miage.tpae.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Permet de préciser les détails d'un virement
 */
@Data
@AllArgsConstructor
public class VirementImport {
    /**
     * Valeur (somme) du virement
     */
    @Schema(description = "Valeur de l'opération", example = "123")
    private double valeur;
    /**
     * Id du compte destinataire
     * Note : le compte de départ est précisé dans l'URL
     */
    @Schema(description = "Id du compte du destinataire", example = "2")
    private long idCompteDestinataire;
}
