package org.miage.tpae.utilities;

/**
 * Exception compte cloturé
 */
public class CompteClotureException extends RuntimeException {
    /**
     * Constructeur
     * @param s message d'erreur
     */
    public CompteClotureException(String s) {
        super(s);
    }
}
