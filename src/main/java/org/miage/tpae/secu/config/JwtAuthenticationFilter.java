package org.miage.tpae.secu.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.miage.tpae.secu.token.TokenRepository;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtre d'authentification
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  // service qui gère les jetons
  private final JwtService jwtService;
  // service qui gère le lien entre la BD et l'utilisateur en cours d'authentification
  private final UserDetailsService userDetailsService;
  // repo pour les jetons
  private final TokenRepository tokenRepository;

  /**
   * filtre proprement dit
   * @param request la requête HTTP
   * @param response objet pour créer la réponse HTTP
   * @param filterChain chaine de filtres
   * @throws ServletException en cas de problème avec la servlet
   * @throws IOException en cas de problème d'E/S
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    // si c'est une requête d'authentification
    if (request.getServletPath().contains("/api/v1/auth")) {
      // on passe directement aux autres filtres
      filterChain.doFilter(request, response);
      return;
    }
    // récupération du jeton depuis l'entête d'autorisation
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;
    // S'il n'y a pas d'entête ou si ce n'est pas un Bearer
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      // on passe aux autres filtres qui génèreront l'erreur d'authentification
      filterChain.doFilter(request, response);
      return;
    }
    // récupère le jeton
    jwt = authHeader.substring(7);
    // récupère le mail à partir du jeton
    userEmail = jwtService.extractUsername(jwt);
    // s'il y a bien un mail et que l'authentification n'a pas déjà été faite
    // Note : la seconde partie sert pour les tests essentiellement
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      // récupère les infos de l'utilisateur depuis la BD
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
      // vérifie si le jeton est bien en BD et s'il n'est ni expiré ni révoqué
      var isTokenValid = tokenRepository.findByToken(jwt)
          .map(t -> !t.isExpired() && !t.isRevoked())
          .orElse(false);
      // vérifie le jeton via JWT
      if (jwtService.isTokenValid(jwt, userDetails) && isTokenValid) {
        // construit un objet d'authentification avec tout ce qui peut être utile
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        // ajoute les infos de la requête HTTP
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        // positionne l'objet d'authentification
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    // on passe aux autres filtres
    filterChain.doFilter(request, response);
  }
}
