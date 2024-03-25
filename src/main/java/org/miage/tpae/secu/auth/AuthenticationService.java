package org.miage.tpae.secu.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.miage.tpae.secu.config.JwtService;
import org.miage.tpae.secu.token.Token;
import org.miage.tpae.secu.token.TokenRepository;
import org.miage.tpae.secu.token.TokenType;
import org.miage.tpae.secu.user.Role;
import org.miage.tpae.secu.user.User;
import org.miage.tpae.secu.user.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Service gérant l'authentification
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  // repo pour les users
  private final UserRepository repository;
  // repo pour les JWT
  private final TokenRepository tokenRepository;
  // pour l'encodage de mot de passe
  private final PasswordEncoder passwordEncoder;
  // service gérant les jetons
  private final JwtService jwtService;
  // Gestionnaire d'authentification
  private final AuthenticationManager authenticationManager;

  /**
   * Enregistrement d'un utilisateur
   * @param request DTO portant les infos d'enregistrement
   * @return réponse d'authentification avec les jetons
   */
  public AuthenticationResponse register(RegisterRequest request) {
    // Construit un utilisateur
    var user = User.builder()
        .firstname(request.getFirstname())
        .lastname(request.getLastname())
        .email(request.getEmail())
        .password(passwordEncoder.encode(request.getPassword()))
        .role(Role.USER)
        .build();
    // sauve l'utilisateur en BD
    var savedUser = repository.save(user);
    // génère les jetons
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    // stocke le jeton d'accès en BD
    saveUserToken(savedUser, jwtToken);
    // construit et retourne la réponse
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  /**
   * Authentification
   * @param request DTO portant les infos d'authentification
   * @return réponse d'authentification avec les jetons
   */
  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    // Gestion de l'authentification pour spring security
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()
        )
    );
    // va chercher les infos de l'utilisateur en BD
    var user = repository.findByEmail(request.getEmail())
        .orElseThrow();
    // construit les jetons
    var jwtToken = jwtService.generateToken(user);
    var refreshToken = jwtService.generateRefreshToken(user);
    // révoque les anciens jetons de l'utilisateur
    revokeAllUserTokens(user);
    // sauve le jeton d'accès en BD
    saveUserToken(user, jwtToken);
    // construit et retourne la réponse avec les jetons
    return AuthenticationResponse.builder()
        .accessToken(jwtToken)
            .refreshToken(refreshToken)
        .build();
  }

  /**
   * Stocke un jeton d'accès en BD
   * @param user
   * @param jwtToken
   */
  private void saveUserToken(User user, String jwtToken) {
    // construit le jeton
    var token = Token.builder()
        .user(user)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
    // le sauve en BD
    tokenRepository.save(token);
  }

  /**
   * Révoque les anciens jetons d'un utilisateur
   * @param user l'utilisateur dont on veut révoquer les anciens jetons
   */
  private void revokeAllUserTokens(User user) {
    // Cherche les anciens jetons
    var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
    // s'il y en a
    if (validUserTokens.isEmpty())
      return;
    // mise à jour en expiré et révoqué
    validUserTokens.forEach(token -> {
      token.setExpired(true);
      token.setRevoked(true);
    });
    // update en BD
    tokenRepository.saveAll(validUserTokens);
  }

  /**
   * Régénère un jeton d'accès
   * @param request requête HTTP avec le jeton de rafraichissement en entête de ce type :
   * Authorization : Bearer <<jeton>
   * @param response objet pour renvoyer la réponse HTTP
   * @throws IOException en cas de pb avec JWT
   */
  public void refreshToken(
          HttpServletRequest request,
          HttpServletResponse response
  ) throws IOException {
    // récupération de l'entête d'autorisation
    final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    final String refreshToken;
    final String userEmail;
    // s'il n'y a pas d'info d'autorisation ou si ce n'est pas un Bearer
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return; // on arrête
    }
    // récupération du jeton
    refreshToken = authHeader.substring(7);
    // récupère l'email à partir du jeton
    userEmail = jwtService.extractUsername(refreshToken);
    // s'il y a bien un mail dans le jeton
    if (userEmail != null) {
      // cherche l'utilisateur correspondant
      var user = this.repository.findByEmail(userEmail)
              .orElseThrow();
      // vérifie que le jeton est encore valide pour cet utilisateur
      if (jwtService.isTokenValid(refreshToken, user)) {
        // génère un jeton d'accès
        var accessToken = jwtService.generateToken(user);
        // révoque les anciens jetons
        revokeAllUserTokens(user);
        // sauve le nouveau jeton en BD
        saveUserToken(user, accessToken);
        // construit la réponse
        var authResponse = AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        // entête de la réponse
        response.setHeader("Content-Type", "application/json");
        // transmission de la réponse
        new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
      }
    }
  }
}
