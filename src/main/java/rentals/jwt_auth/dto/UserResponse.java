package rentals.jwt_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "Représentation des informations renvoyées pour un utilisateur")
public record UserResponse(

    @Schema(description = "Identifiant unique de l'utilisateur", example = "42")
    Long id,

    @Schema(description = "Nom complet de l'utilisateur", example = "John Doe")
    String name,

    @Schema(description = "Adresse email unique de l'utilisateur", example = "john.doe@example.com")
    String email,

    @Schema(description = "Rôle attribué à l'utilisateur (ex: ROLE_USER, ROLE_ADMIN)", example = "ROLE_USER")
    String role,

    @Schema(description = "Date et heure de création du compte", example = "2025-08-09T12:34:56")
    LocalDateTime created_at,

    @Schema(description = "Date et heure de la dernière mise à jour du compte", example = "2025-08-09T15:45:30")
    LocalDateTime updated_at
) {}
