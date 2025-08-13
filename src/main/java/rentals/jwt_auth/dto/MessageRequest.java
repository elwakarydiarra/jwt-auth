package rentals.jwt_auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Requête de création/mise à jour d'un message")
public class MessageRequest {

    @Schema(description = "Identifiant de la location associée", example = "42")
    private Long rentalId;

    @Schema(description = "Contenu du message", example = "Bonjour, est-il disponible ?")
    private String message;

    public Long getRentalId() { return rentalId; }
    public void setRentalId(Long rentalId) { this.rentalId = rentalId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
