package org.miage.tpae.utilities;

/**
 * Exception opération non conforme
 */
public class OperationNonConforme extends RuntimeException {
    /**
     * Constructeur
     * @param s message d'erreur
     */
    public OperationNonConforme(String s) {
        super(s);
    }
}
