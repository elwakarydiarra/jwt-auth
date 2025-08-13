package rentals.jwt_auth.dto;

import java.time.LocalDateTime;

public record MessageResponse(
    Long id,
    Long rentalId,
    Long userId,
    String message,
    LocalDateTime createdAt
) {}
