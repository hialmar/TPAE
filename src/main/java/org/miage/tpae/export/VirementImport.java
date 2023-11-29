package org.miage.tpae.export;

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
    private double valeur;
    /**
     * Id du compte destinataire
     * Note : le compte de départ est précisé dans l'URL
     */
    private long idCompteDestinataire;
}
