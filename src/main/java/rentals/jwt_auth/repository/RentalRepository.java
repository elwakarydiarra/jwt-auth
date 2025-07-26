package rentals.jwt_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rentals.jwt_auth.model.Rental;

public interface RentalRepository extends JpaRepository<Rental, Long> {
}
