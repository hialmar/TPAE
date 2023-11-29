package org.miage.tpae.export;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Classe représentant les erreurs en JSON
 */
@Data
@AllArgsConstructor
public class ErrorExport {
    /**
     * Message d'erreur
     */
    private final String message;
    /**
     * Type de l'exception
     */
    private final String exceptionType;
}
