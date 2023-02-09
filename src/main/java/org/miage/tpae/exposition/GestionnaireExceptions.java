package org.miage.tpae.exposition;

import jakarta.servlet.http.HttpServletRequest;
import org.miage.tpae.export.ErrorExport;
import org.miage.tpae.utilities.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Gestionnaire d'exceptions pour envoyer les bonnes erreurs HTTP
 */
@ControllerAdvice
public class GestionnaireExceptions {

    /**
     * Erreur 404 en cas de Compte Inconnu
     * @param request requête HTTP
     * @param exception exception
     * @return l'erreur 404
     */
    @ExceptionHandler(CompteInconnuException.class)
    public ResponseEntity<ErrorExport> gereAutreException(HttpServletRequest request, CompteInconnuException exception) {
        return new ResponseEntity<>(new ErrorExport(exception.getMessage(), exception.getClass().getName()), HttpStatus.NOT_FOUND);
    }

    /**
     * Erreur 404 en cas de Client Inconnu
     * @param request requête HTTP
     * @param exception exception
     * @return l'erreur 404
     */
    @ExceptionHandler(ClientInexistant.class)
    public ResponseEntity<ErrorExport> gereAutreException(HttpServletRequest request, ClientInexistant exception) {
        return new ResponseEntity<>(new ErrorExport(exception.getMessage(), exception.getClass().getName()), HttpStatus.NOT_FOUND);
    }


    /**
     * Erreur 404 en cas de Client Inconnu
     * Note : c'est cette erreur qui est générée lors de la transformation automatique
     * de l'id en Client
     * @param request requête HTTP
     * @param exception exception
     * @return l'erreur 404
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorExport> gereAutreException(HttpServletRequest request, MissingPathVariableException exception) {
        return new ResponseEntity<>(new ErrorExport(exception.getMessage(), exception.getClass().getName()), HttpStatus.NOT_FOUND);
    }

    /**
     * Erreur 400 en cas de compte cloturé
     * @param request requête HTTP
     * @param exception exception
     * @return l'erreur 400
     */
    @ExceptionHandler(CompteClotureException.class)
    public ResponseEntity<ErrorExport> gereAutreException(HttpServletRequest request, CompteClotureException exception) {
        return new ResponseEntity<>(new ErrorExport(exception.getMessage(), exception.getClass().getName()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Erreur 400 en cas de montant invalide
     * @param request requête HTTP
     * @param exception exception
     * @return l'erreur 400
     */
    @ExceptionHandler(MontantInvalidException.class)
    public ResponseEntity<ErrorExport> gereAutreException(HttpServletRequest request, MontantInvalidException exception) {
        return new ResponseEntity<>(new ErrorExport(exception.getMessage(), exception.getClass().getName()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Erreur 400 en cas de solde insuffisant
     * @param request requête HTTP
     * @param exception exception
     * @return l'erreur 400
     */
    @ExceptionHandler(SoldeInsuffisantException.class)
    public ResponseEntity<ErrorExport> gereAutreException(HttpServletRequest request, SoldeInsuffisantException exception) {
        return new ResponseEntity<>(new ErrorExport(exception.getMessage(), exception.getClass().getName()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Erreur 400 en cas d'opération non conforme
     * @param request requête HTTP
     * @param exception exception
     * @return l'erreur 400
     */
    @ExceptionHandler(OperationNonConforme.class)
    public ResponseEntity<ErrorExport> gereAutreException(HttpServletRequest request, OperationNonConforme exception) {
        return new ResponseEntity<>(new ErrorExport(exception.getMessage(), exception.getClass().getName()), HttpStatus.BAD_REQUEST);
    }

    /**
     * Erreur 500 en cas d'autre erreur
     * @param request requête HTTP
     * @param exception exception
     * @return l'erreur 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorExport> gereAutreException(HttpServletRequest request, Exception exception) {
        return new ResponseEntity<>(new ErrorExport(exception.getMessage(), exception.getClass().getName()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
