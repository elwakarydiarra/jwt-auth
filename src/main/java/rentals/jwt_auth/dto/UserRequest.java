package rentals.jwt_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Représentation des informations d'un utilisateur")
public class UserRequest {

    @Schema(
        description = "Adresse email unique de l'utilisateur",
        example = "john.doe@example.com"
    )
    private String email;

    @Schema(
        description = "Nom complet de l'utilisateur",
        example = "John Doe"
    )
    private String name;

    @Schema(
        description = "Rôle attribué à l'utilisateur (exemple: ROLE_USER ou ROLE_ADMIN)",
        example = "ROLE_USER"
    )
    private String role;

    @Schema(
        description = "Date et heure de création du compte",
        example = "2025-08-09T12:34:56"
    )
    private LocalDateTime created_at;

    @Schema(
        description = "Date et heure de la dernière mise à jour du compte",
        example = "2025-08-09T15:45:30"
    )
    private LocalDateTime updated_at;
}
