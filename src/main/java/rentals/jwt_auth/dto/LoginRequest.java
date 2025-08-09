package rentals.jwt_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Schema(description = "Requête de connexion pour un utilisateur")

public class LoginRequest {
	
	@Schema(
	        description = "Adresse email utilisée pour se connecter",
	        example = "john.doe@example.com"
	    )
    private String email;
	
	@Schema(
	        description = "Mot de passe associé au compte utilisateur",
	        example = "P@ssw0rd!"
	        )
    private String password;

}
