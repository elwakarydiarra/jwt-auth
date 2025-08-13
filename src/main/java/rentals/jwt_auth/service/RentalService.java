package rentals.jwt_auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rentals.jwt_auth.model.Rental;
import rentals.jwt_auth.model.User;
import rentals.jwt_auth.repository.RentalRepository;
// import rentals.jwt_auth.repository.UserRepository;
// import rentals.jwt_auth.model.User;
import rentals.jwt_auth.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;

    public RentalService(RentalRepository rentalRepository, UserRepository userRepository) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Rental> getAllRentals() {
        return rentalRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rental> getRentalById(Long id) {
        return rentalRepository.findById(id);
    }

    public Rental createRental(Rental rental) {
        return rentalRepository.save(rental);
    }

    public Rental createRental(Rental rental, String picturePath) {
        if (picturePath != null) {
            rental.setPicture(picturePath);
        }
        return rentalRepository.save(rental);
    }

    public Rental createRentalForUser(Rental rental, String username) {
        return createRentalForUser(rental, username, null);
    }

    public Rental createRentalForUser(Rental rental, String username, String picturePath) {
        if (username != null && !username.isBlank()) {
            User owner = userRepository.findByEmail(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
            rental.setOwner(owner);
        }

        if (picturePath != null) {
            rental.setPicture(picturePath);
        }
        return rentalRepository.save(rental);
    }

    public Optional<Rental> updateRental(Long id, Rental updated) {
        return updateRental(id, updated, null);
    }

    public Optional<Rental> updateRental(Long id, Rental updated, String picturePath) {
        return rentalRepository.findById(id).map(existing -> {
            if (updated != null) {
                if (updated.getName() != null) existing.setName(updated.getName());
                if (updated.getSurface() != null) existing.setSurface(updated.getSurface());
                if (updated.getPrice() != null) existing.setPrice(updated.getPrice());
                if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
            }
            if (picturePath != null) {
                existing.setPicture(picturePath);
            }
            return rentalRepository.save(existing);
        });
    }

    public void deleteRental(Long id) {
        rentalRepository.deleteById(id);
    }
}
