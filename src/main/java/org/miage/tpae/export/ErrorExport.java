package org.miage.tpae.export;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Message d'erreur", example = "Le client n'existe pas")
    private final String message;
    /**
     * Type de l'exception
     */
    @Schema(description = "Type d'exception", example = "ClientInexistant")
    private final String exceptionType;
}
