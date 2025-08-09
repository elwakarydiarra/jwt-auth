package rentals.jwt_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour inscrire un nouvel utilisateur")
public class RegisterRequest {

    @Schema(
        description = "Adresse email unique de l'utilisateur",
        example = "john.doe@example.com"
    )
    private String email;

    @Schema(
        description = "Mot de passe de l'utilisateur (minimum 6 caractères recommandés)",
        example = "P@ssw0rd"
    )
    private String password;

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
}
