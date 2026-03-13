package org.miage.tpae.secu.auth;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.miage.tpae.export.OperationImport;
import org.miage.tpae.export.Position;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Controller qui gère l'authentification
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@OpenAPIDefinition(
        info = @Info( title = "Service d'authentification",
                description = "Service gérant l'authentification et la (ré)génération des jetons d'authentification",
                contact = @Contact(name = "Patrice Torguet", email = "patrice.torguet@irit.fr"),
                version = "0.1"))
public class AuthenticationController {

  /**
   * Service d'authentification
   */
  private final AuthenticationService service;

  /**
   * Gestion de l'enregistrement
   * Il faut faire un POST sur http://localhost:8080/api/v1/auth/register
   * Avec comme type de JSON :
   * {"firstname": "toto","lastname": "toto","email": "toto@toto.com","password": "password"}
   * @param request requête HTTP
   * @return réponse de type : {
   * "access_token": "JWT pour l'accès aux différentes ressources protégées",
   * "refresh_token": "JWT pour récupérer un nouveau token en cas d'expiration"
   * }
   */
  @PostMapping("/register")
  @Operation(summary = "Créer un compte d'authentification",
          description = "Permet de s'enregistrer",
          tags = { "authentification" },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Infos du compte d'authentification",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = RegisterRequest.class)),
                  required = true))
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Jetons d'authentification",
                  content = { @Content(mediaType = "application/json",
                          schema = @Schema(implementation = AuthenticationResponse.class)) })})
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }

  /**
   * Gestion de l'authentification
   * Il faut faire un POST sur http://localhost:8080/api/v1/auth/authenticate
   * Avec comme type de JSON :
   * {"email": "toto@toto.com","password": "password"}
   *
   * @param request requête HTTP
   * @return réponse de type : {
   *   "access_token": "JWT pour l'accès aux différentes ressources protégées",
   *   "refresh_token": "JWT pour récupérer un nouveau token en cas d'expiration"
   * }
   */
  @PostMapping("/authenticate")
  @Operation(summary = "Authentification",
          description = "Permet de s'authentifier",
          tags = { "authentification" },
          requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                  description = "Requête d'authentification",
                  content = @Content(mediaType = "application/json",
                          schema = @Schema(implementation = AuthenticationRequest.class)),
                  required = true))
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Jetons d'authentification",
                  content = { @Content(mediaType = "application/json",
                          schema = @Schema(implementation = AuthenticationResponse.class)) })})
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  /**
   * Gestion du rafraichissement des JWT expirés
   * @param request la requête HTTP
   * @param response pour créer la réponse HTTP
   * @throws IOException en cas de problème avec JWT
   */
  @PostMapping("/refresh-token")
  @Operation(summary = "Régénération du jeton d'authentification",
          description = "Permet de regénérer les jetons d'authentification",
          tags = { "authentification" })
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Jetons d'authentification",
                  content = { @Content(mediaType = "application/json",
                          schema = @Schema(implementation = AuthenticationResponse.class)) })})
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
