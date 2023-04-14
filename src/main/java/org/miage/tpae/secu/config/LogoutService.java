package org.miage.tpae.secu.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.miage.tpae.secu.token.TokenRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

/**
 * Gestionnaire de déconnexion
 */
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  // repo des tokens
  private final TokenRepository tokenRepository;

  /**
   * Méthode gérant la déconnexion
   * @param request la requête HTTP
   * @param response objet pour construire la réponse
   * @param authentication infos d'authentification
   */
  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    // récupère l'entête d'autorisation
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    // s'il n'y a pas d'entête ou si ce n'est pas un Bearer
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      // on ne fait rien
      return;
    }
    // récupère le jeton
    jwt = authHeader.substring(7);
    // va chercher le jeton en BD
    var storedToken = tokenRepository.findByToken(jwt)
        .orElse(null);
    // s'il y en a un
    if (storedToken != null) {
      // le passe en expiré et révoqué
      storedToken.setExpired(true);
      storedToken.setRevoked(true);
      // sauvegarde en BD
      tokenRepository.save(storedToken);
      // enlève les infos d'authentification
      SecurityContextHolder.clearContext();
    }
  }
}
