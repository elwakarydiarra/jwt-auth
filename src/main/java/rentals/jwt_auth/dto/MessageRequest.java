package rentals.jwt_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Requête pour envoyer un message concernant une location")
public class MessageRequest {

    @Schema(
        description = "Identifiant unique de la location associée au message",
        example = "42"
    )
    private Long rentalId;

    @Schema(
        description = "Contenu du message envoyé par l'utilisateur",
        example = "Bonjour, je suis intéressé par votre appartement. Est-il toujours disponible ?"
    )
    private String message;
}
