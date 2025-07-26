package rentals.jwt_auth.dto;

public class MessageRequest {
    private Long rentalId;
    private String message;

    // Getters et Setters
    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
