package rentals.jwt_auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public String getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return "👤 Utilisateur connecté : " + userDetails.getUsername();
    }
}
