package rentals.jwt_auth.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import rentals.jwt_auth.dto.MessageRequest;
import rentals.jwt_auth.model.Message;
import rentals.jwt_auth.model.User;
import rentals.jwt_auth.repository.UserRepository;
import rentals.jwt_auth.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserRepository userRepository;

    public MessageController(MessageService messageService, UserRepository userRepository) {
        this.messageService = messageService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Envoyer un message pour une location", responses = {
            @ApiResponse(responseCode = "200", description = "Message enregistré")
        })
    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody MessageRequest request,
                                               @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Message message = messageService.sendMessage(request, user.getId());

        return ResponseEntity.ok(message);
    }
    
    @Operation(summary = "Lister les messages de l'utilisateur connecté", responses = {
            @ApiResponse(responseCode = "200", description = "Liste des messages")
        })
    @GetMapping
    public ResponseEntity<List<Message>> getUserMessages(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                      .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        List<Message> messages = messageService.getMessagesByUserId(user.getId());
        return ResponseEntity.ok(messages);
    }



}
