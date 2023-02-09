package org.miage.tpae.utilities;

/**
 * Exception solde insuffisant
 */
public class SoldeInsuffisantException extends RuntimeException {
    /**
     * Constructeur
     * @param s message d'erreur
     */
    public SoldeInsuffisantException(String s) {
        super(s);
    }
}
