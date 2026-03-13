package org.miage.tpae.secu.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corps d'une requête d'authentification (DTO)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

  @Schema(description = "Email de la personne", example = "jean@martin.name")
  private String email;
  @Schema(description = "Mot de passe de la personne", example = "1234")
  String password;
}
