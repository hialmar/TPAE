package org.miage.tpae.secu.config;

import lombok.RequiredArgsConstructor;
import org.miage.tpae.secu.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration des outils de sécurité
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

  // repo pour les utilisateurs
  private final UserRepository repository;

  /**
   * Crée le bean qui va chercher les données des utilisateurs en BD lors de l'authentification
   * @return le bean
   */
  @Bean
  public UserDetailsService userDetailsService() {
    return username -> repository.findByEmail(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }

  /**
   * Crée le bean fournisseur d'authentification
   * @return le bean
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {
    // fournisseur d'authentification lié à une BD
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    // on lui donne le bean qui va aller chercher les données en BD
    authProvider.setUserDetailsService(userDetailsService());
    // on lui donne l'encodeur de mot de passe
    authProvider.setPasswordEncoder(passwordEncoder());
    // retourne le bean
    return authProvider;
  }

  /**
   * Crée le bean qui gère l'authentification
   * @param config paramètres de configuration d'authentification
   * @return le bean
   * @throws Exception s'il y a eu un problème lors de la création du bean
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  /**
   * Crée le bean d'encodage de mot de passe
   * @return le bean
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
