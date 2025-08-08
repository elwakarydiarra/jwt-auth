package rentals.jwt_auth.controller;

import rentals.jwt_auth.dto.LoginRequest;
import rentals.jwt_auth.dto.RegisterRequest;
import rentals.jwt_auth.service.AuthService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "${cors.allowed-origins}", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Inscription d’un nouvel utilisateur", responses = {
            @ApiResponse(responseCode = "200", description = "Token JWT renvoyé après enregistrement")
        })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequest request) {
        String token = authService.register(request);
        return ResponseEntity.ok(Map.of("token", token));
    }
    
    @Operation(summary = "Connexion d’un utilisateur", responses = {
            @ApiResponse(responseCode = "200", description = "Token JWT renvoyé après connexion"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
        })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(Map.of("token", token));
    }
    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> me(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails) {
        return ResponseEntity.ok(Map.of("email", userDetails.getUsername()));
    }

}
