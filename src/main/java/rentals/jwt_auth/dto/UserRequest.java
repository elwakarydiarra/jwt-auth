package rentals.jwt_auth.dto;
import lombok.*;
import java.time.LocalDateTime;


@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserRequest {

    private String email;
    private String name;
    private String role;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
