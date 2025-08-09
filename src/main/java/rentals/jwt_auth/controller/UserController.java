package rentals.jwt_auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import rentals.jwt_auth.dto.UserRequest;
import rentals.jwt_auth.dto.UserResponse;
import rentals.jwt_auth.repository.UserRepository;
import rentals.jwt_auth.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    
    
    @Operation(
            summary = "Récupérer l'utilisateur connecté",
            description = "Retourne l'utilisateur correspondant au token JWT.",
            responses = {
                    	@ApiResponse(responseCode = "200", description = "Utilisateur courant"),
                        @ApiResponse(responseCode = "401", description = "Non authentifié")
        })
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserDetails userDetails) {
        var u = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        var dto = new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRole(),
                u.getCreated_at(),
                u.getUpdated_at()
        );
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Récupérer un utilisateur par id",
            description = "Renvoie une projection UserRequest (DTO léger).",
            responses = {
                    	@ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
                        @ApiResponse(responseCode = "404", description = "Utilisateur introuvable"),
                        @ApiResponse(responseCode = "401", description = "Non authentifié")
        })
    @GetMapping("/{id}")
    public ResponseEntity<UserRequest> getById(@PathVariable Long id) {
        return userService.getUserRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
