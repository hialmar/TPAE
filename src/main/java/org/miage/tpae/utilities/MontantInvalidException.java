package org.miage.tpae.utilities;

/**
 * Exception montant invalide
 */
public class MontantInvalidException extends RuntimeException {
    /**
     * Constructeur
     * @param s message d'erreur
     */
    public MontantInvalidException(String s) {
        super(s);
    }
}
