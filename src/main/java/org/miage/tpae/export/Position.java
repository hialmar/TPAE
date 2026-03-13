package org.miage.tpae.export;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Calendar;

/**
 * Permet de récupérer les détails utiles d'un compte
 */
@Data
@AllArgsConstructor
public class Position {
    /**
     * Solde du compte
     */
    @Schema(description = "Solde d'un compte", example = "1000")
    private double solde;
    /**
     * Date de la consultation
     */
    @Schema(description = "Date d'interrogation du compte", example = "2026-03-13T08:38:20.296Z")
    private Calendar dateInterrogation;
}
