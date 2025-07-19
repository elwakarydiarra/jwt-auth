package rentals.jwt_auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rentals.jwt_auth.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
