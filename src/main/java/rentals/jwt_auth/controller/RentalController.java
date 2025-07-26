package rentals.jwt_auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import rentals.jwt_auth.model.Rental;
import rentals.jwt_auth.repository.RentalRepository;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalRepository rentalRepository;

    public RentalController(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }
    
    @PostMapping
    public ResponseEntity<Rental> createRental(@RequestBody Rental rental) {
        Rental savedRental = rentalRepository.save(rental);
        return ResponseEntity.ok(savedRental);
    }

    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals(@AuthenticationPrincipal UserDetails userDetails) {
        // Juste pour confirmer que l'utilisateur est authentifié (facultatif)
        System.out.println("👤 Utilisateur authentifié : " + userDetails.getUsername());

        List<Rental> rentals = rentalRepository.findAll();
        return ResponseEntity.ok(rentals);
    }
}
