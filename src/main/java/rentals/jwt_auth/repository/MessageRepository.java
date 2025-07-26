package rentals.jwt_auth.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import rentals.jwt_auth.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByUserId(Long userId);
}
