package org.miage.tpae.secu.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
