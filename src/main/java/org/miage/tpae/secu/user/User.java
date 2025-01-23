package org.miage.tpae.secu.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.tpae.secu.token.Token;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entité correspondant à un utilisateur
 * Dérive de UserDetails
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "_user")
public class User implements UserDetails {

  /**
   * Id de l'utilisateur
   */
  @Id
  @GeneratedValue
  private Integer id;
  /**
   * Prénom
   */
  private String firstname;
  /**
   * Nom
   */
  private String lastname;
  /**
   * Email utilisé pour se connecter
   */
  private String email;
  /**
   * Mot de passe (chiffré)
   */
  private String password;

  /**
   * Role de l'utilisateur
   */
  @Enumerated(EnumType.STRING)
  private Role role;

  /**
   * Liste des jetons de cet utilisateur
   */
  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

  /**
   * Authorités == rôles (RBAC) de cet utilisateur
   * @return collection d'authorités
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  /**
   * Permet de récupérer le mot de passe
   * @return le mot de passe
   */
  @Override
  public String getPassword() {
    return password;
  }

  /**
   * Permet de récupérer l'e-mail de l'utilisateur
   * @return l'e-mail
   */
  @Override
  public String getUsername() {
    return email;
  }

  /**
   * Est-ce que le compte n'est pas expiré
   * @return true (non géré)
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * Est-ce que le compte est non bloqué temporairement
   * @return true (non géré)
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * Est-ce que les autorisations n'ont pas expiré
   * @return true (non géré)
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * Est-il actif
   * @return true (non géré)
   */
  @Override
  public boolean isEnabled() {
    return true;
  }
}
