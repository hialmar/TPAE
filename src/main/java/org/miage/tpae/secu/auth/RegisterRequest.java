package org.miage.tpae.secu.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corps d'une requête d'enregistrement (DTO)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

  @Schema(description = "Prénom de la personne", example = "Jean")
  private String firstname;
  @Schema(description = "Nom de la personne", example = "Martin")
  private String lastname;
  @Schema(description = "Adresse email de la personne", example = "jean@martin.name")
  private String email;
  @Schema(description = "Mot de passe de la personne", example = "1234")
  private String password;
}
