package rentals.jwt_auth.dto;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor

public class MessageRequest {
    private Long rentalId;
    private String message;

}
