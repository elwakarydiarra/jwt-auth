package rentals.jwt_auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import rentals.jwt_auth.dto.MessageRequest;
import rentals.jwt_auth.dto.MessageResponse;
import rentals.jwt_auth.model.Message;
import rentals.jwt_auth.service.MessageService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/messages", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Messages", description = "CRUD des messages (IDs simples)")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    @Operation(summary = "Lister les messages")
    @ApiResponse(responseCode = "200", description = "OK")
    public ResponseEntity<List<MessageResponse>> getAll() {
        List<MessageResponse> out = messageService.getAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail message")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "404", description = "Introuvable")
    public ResponseEntity<MessageResponse> getById(@PathVariable Long id) {
        return messageService.getById(id)
                .map(m -> ResponseEntity.ok(toResponse(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un message (JSON)")
    @ApiResponse(responseCode = "201", description = "Créé")
    public ResponseEntity<MessageResponse> create(@RequestBody MessageRequest req,
                                                  @AuthenticationPrincipal UserDetails principal) {
        String email = principal != null ? principal.getUsername() : null;
        Message created = messageService.create(req, email);
        return ResponseEntity.status(201).body(toResponse(created));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un message (JSON)")
    @ApiResponse(responseCode = "200", description = "Mis à jour")
    public ResponseEntity<MessageResponse> update(@PathVariable Long id, @RequestBody MessageRequest req) {
        return messageService.update(id, req)
                .map(m -> ResponseEntity.ok(toResponse(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un message")
    @ApiResponse(responseCode = "204", description = "Supprimé")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        messageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Supprimer tous les messages (danger)")
    @ApiResponse(responseCode = "204", description = "Supprimés")
    public ResponseEntity<Void> deleteAll() {
        messageService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    private MessageResponse toResponse(Message m) {
        java.time.LocalDateTime createdAt = null;
        try {
            var getter = m.getClass().getMethod("getCreatedAt");
            var v = getter.invoke(m);
            if (v instanceof java.time.LocalDateTime) createdAt = (java.time.LocalDateTime) v;
        } catch (Exception ignore) {}
        return new MessageResponse(m.getId(), m.getRentalId(), m.getUserId(), m.getMessage(), createdAt);
    }
}
