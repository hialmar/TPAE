package org.miage.tpae.export;

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
    private double solde;
    /**
     * Date de la consultation
     */
    private Calendar dateInterrogation;
}
