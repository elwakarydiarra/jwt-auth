package rentals.jwt_auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import rentals.jwt_auth.model.User;
import rentals.jwt_auth.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
