package rentals.jwt_auth.dto;

import java.time.LocalDateTime;


public record UserResponse(
    Long id,
    String name,
    String email,
    String role,
    LocalDateTime created_at,
    LocalDateTime updated_at
) {}
