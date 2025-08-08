package rentals.jwt_auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import rentals.jwt_auth.dto.UserRequest;
import rentals.jwt_auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Récupérer l'utilisateur connecté", responses = {
            @ApiResponse(responseCode = "200", description = "Informations de l'utilisateur connecté")
        })
    @GetMapping("/me")
    public ResponseEntity<UserRequest> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserRequestByEmail(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
