package org.miage.tpae.secu.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Réponse d'authentification portant des JWT
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  @JsonProperty("access_token")
  @Schema(description = "Jeton d'accès", example = "xxxx")
  private String accessToken;
  @JsonProperty("refresh_token")
  @Schema(description = "Jeton de régénération", example = "yyyy")
  private String refreshToken;
}
