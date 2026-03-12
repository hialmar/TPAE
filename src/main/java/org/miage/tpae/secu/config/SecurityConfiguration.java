package org.miage.tpae.secu.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

/**
 * Configuration de Sécurité.
 * Toute la partie sécurité vient de :
 * <a href="https://github.com/ali-bouali/spring-boot-3-jwt-security">Repo Github sur la sécurité avec JWT et Spring Boot 3</a>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

  // Les filtres d'authentification
  private final JwtAuthenticationFilter jwtAuthFilter;
  // fournisseur d'authentification
  private final AuthenticationProvider authenticationProvider;
  // gestionnaire de déconnexion
  private final LogoutHandler logoutHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // pour désactiver la sécurité
    boolean desactiveSecu = false;

    if (desactiveSecu) {

      http
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/**").permitAll());

    }
      http
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth
                      .requestMatchers("/api/v1/auth/**").permitAll()
                      .requestMatchers("/**").authenticated());
      http.sessionManagement(sess -> sess
                      .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
              .authenticationProvider(authenticationProvider) // précise le fournisseur d'auth
              .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // et le filtre
              .logout((logout) -> logout.logoutUrl("/api/v1/auth/logout")
                      .addLogoutHandler(logoutHandler) // précise le gestionnaire de déconnexion
                      .logoutSuccessHandler(
                              (request, response, authentication)
                                      -> SecurityContextHolder.clearContext()));

    return http.build();
  }
}
