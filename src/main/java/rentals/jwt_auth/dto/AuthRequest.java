package rentals.jwt_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Requête d'authentification pour obtenir un token JWT")
public class AuthRequest {
	
	@Schema(description = "Nom d'utilisateur ou email de l'utilisateur",
            example = "john.doe@example.com")
    private String username;
	
	@Schema(description = "Mot de passe associé au compte utilisateur",
            example = "P@ssw0rd!")
    private String password;

}
