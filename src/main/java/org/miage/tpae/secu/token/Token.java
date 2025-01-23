package org.miage.tpae.secu.token;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.miage.tpae.secu.user.User;

/**
 * Jeton stocké en BD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Token {

  /**
   * Id du jeton
   */
  @Id
  @GeneratedValue
  public Integer id;

  /**
   * Le jeton proprement dit
   */
  @Column(unique = true)
  public String token;

  /**
   * Type de jeton
   */
  @Enumerated(EnumType.STRING)
  public TokenType tokenType = TokenType.BEARER;

  /**
   * Est-ce qu'il est révoqué ?
   */
  public boolean revoked;

  /**
   * Est-ce qu'il a expiré
   */
  public boolean expired;

  /**
   * L'utilisateur qui possède ce jeton
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
}
