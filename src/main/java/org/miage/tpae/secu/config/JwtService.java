package org.miage.tpae.secu.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service gérant les jetons JWT
 */
@Service
public class JwtService {

  // clé d'encodage des jetons
  @Value("${application.security.jwt.secret-key}")
  private String secretKey;
  // expiration des jetons d'accès
  @Value("${application.security.jwt.expiration}")
  private long jwtExpiration;
  // expiration des jetons de rafraichissement
  @Value("${application.security.jwt.refresh-token.expiration}")
  private long refreshExpiration;

  /**
   * Extraction du nom de l'utilisateur (ici un email)
   * @param token le jeton
   * @return le nom
   */
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  /**
   * Extraction proprement dite
   * @param token le token
   * @param claimsResolver la méthode d'extraction
   * @return données extraites
   * @param <T> type des données extraites
   */
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  /**
   * Génération de jeton à partir des données de l'utilisateur
   * @param userDetails données de l'utilisateur
   * @return le jeton
   */
  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  /**
   * Génération du jeton
   * @param extraClaims autres infos à ajouter
   * @param userDetails infos de base
   * @return le jeton
   */
  public String generateToken(
      Map<String, Object> extraClaims,
      UserDetails userDetails
  ) {
    return buildToken(extraClaims, userDetails, jwtExpiration);
  }

  /**
   * Génération du jeton de rafraichissement
   * @param userDetails données de l'utilisateur
   * @return le jeton
   */
  public String generateRefreshToken(
      UserDetails userDetails
  ) {
    return buildToken(new HashMap<>(), userDetails, refreshExpiration);
  }

  /**
   * Utilisation de JWT pour générer les jetons
   * @param extraClaims données suppléementaires
   * @param userDetails données de base
   * @param expiration expiration en ms
   * @return le jeton
   */
  private String buildToken(
          Map<String, Object> extraClaims,
          UserDetails userDetails,
          long expiration
  ) {
    // génération du jeton avec l'algo HS256
    return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
  }

  /**
   * Vérifie si un jeton est valide (même email et non expiré)
   * @param token le jeton
   * @param userDetails les données de l'utilisateur
   * @return vrai s'il est valide
   */
  public boolean isTokenValid(String token, UserDetails userDetails) {
    // récupère le nom depuis le jeton
    final String username = extractUsername(token);
    // vérifie si c'est le même et si le jeton n'est pas expiré
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
  }

  /**
   * Contrôle l'expiration du jeton
   * @param token le jeton
   * @return vrai s'il est expiré
   */
  private boolean isTokenExpired(String token) {
    // vérifie que la date d'expiration est dans le passé
    return extractExpiration(token).before(new Date());
  }

  /**
   * Extraction de la date d'expiration
   * @param token le jeton
   * @return la date d'expiration
   */
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  /**
   * Extraction de toutes les infos depuis un jeton
   * @param token le jeton
   * @return les infos
   */
  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Construit la clé de signature (clé publique) à partir de la clé secrète
   * @return la clé
   */
  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
