package org.miage.tpae.utilities;

/**
 * Exception compte inexistant
 */
public class ClientInexistant extends RuntimeException {
    /**
     * Constructeur
     * @param s message d'erreur
     */
    public ClientInexistant(String s) {
        super(s);
    }
}
