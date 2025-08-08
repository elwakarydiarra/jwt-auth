package rentals.jwt_auth.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import rentals.jwt_auth.repository.UserRepository;
import rentals.jwt_auth.dto.UserRequest;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserRequest> getUserRequestByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(user -> {
                UserRequest dto = new UserRequest();
                dto.setName(user.getName());
                dto.setEmail(user.getEmail());
                dto.setRole(user.getRole());
                dto.setCreated_at(user.getCreated_at());
                dto.setUpdated_at(user.getUpdated_at());
                return dto;
            });
    }

}
