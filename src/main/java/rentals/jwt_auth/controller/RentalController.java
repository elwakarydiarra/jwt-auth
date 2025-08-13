package rentals.jwt_auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rentals.jwt_auth.model.Rental;
import rentals.jwt_auth.service.RentalService;
import rentals.jwt_auth.service.StorageService;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping(value = "/api/rentals", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Rentals", description = "CRUD des locations (pattern couches)")
public class RentalController {

    private final RentalService rentalService;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;

    public RentalController(RentalService rentalService, ObjectMapper objectMapper, StorageService storageService) {
        this.rentalService = rentalService;
        this.objectMapper = objectMapper;
        this.storageService = storageService;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les locations",
            responses = { @ApiResponse(responseCode = "200", description = "OK") })
    public ResponseEntity<List<Rental>> getAll() {
        List<Rental> rentals = rentalService.getAllRentals();
        String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        for (Rental r : rentals) {
            if (r.getPicture() != null && !r.getPicture().startsWith("http")) {
                r.setPicture(base + r.getPicture());
            }
        }
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détails d’une location",
            responses = {
                @ApiResponse(responseCode = "200", description = "OK"),
                @ApiResponse(responseCode = "404", description = "Introuvable")
            })
    public ResponseEntity<Rental> getById(@PathVariable Long id) {
        return rentalService.getRentalById(id)
                .map(r -> {
                    String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                    if (r.getPicture() != null && !r.getPicture().startsWith("http")) {
                        r.setPicture(base + r.getPicture());
                    }
                    return ResponseEntity.ok(r);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une location (JSON)")
    @ApiResponse(responseCode = "201", description = "Créée")
    public ResponseEntity<Rental> createJson(@RequestBody Rental rental,
                                             @AuthenticationPrincipal UserDetails principal) {
        String username = principal != null ? principal.getUsername() : null;
        Rental created = rentalService.createRentalForUser(rental, username, null);
        String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        if (created.getPicture() != null && !created.getPicture().startsWith("http")) {
            created.setPicture(base + created.getPicture());
        }
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une location (multipart)", description = "Accepte soit 'rental' (JSON), soit des champs à plat.")
    @ApiResponse(responseCode = "201", description = "Créée")
    public ResponseEntity<Rental> createMultipart(
            @AuthenticationPrincipal UserDetails principal,
            @RequestPart(value = "rental", required = false) String rentalJson,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surface", required = false) Double surface,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "picture", required = false) MultipartFile picture
    ) {
        try {
            Rental rental = buildRentalFromMultipartStrict(rentalJson, name, surface, price, description);
            if (rental == null) {
                return ResponseEntity.badRequest().build();
            }
            String picturePath = null;
            if (picture != null && !picture.isEmpty()) {
                try { picturePath = storageService.savePicture(picture); } catch (Exception ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); }
            }
            String username = principal != null ? principal.getUsername() : null;
            Rental created = rentalService.createRentalForUser(rental, username, picturePath);
            String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
            if (created.getPicture() != null && !created.getPicture().startsWith("http")) {
                created.setPicture(base + created.getPicture());
            }
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(created.getId())
                    .toUri();
            return ResponseEntity.created(location).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une location (JSON)")
    @ApiResponse(responseCode = "200", description = "Mise à jour")
    public ResponseEntity<Rental> updateJson(@PathVariable Long id, @RequestBody Rental rental) {
        return rentalService.updateRental(id, rental)
                .map(r -> {
                    String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                    if (r.getPicture() != null && !r.getPicture().startsWith("http")) {
                        r.setPicture(base + r.getPicture());
                    }
                    return ResponseEntity.ok(r);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une location (multipart)", description = "Accepte soit 'rental' (JSON), soit des champs à plat. Mise à jour partielle autorisée.")
    @ApiResponse(responseCode = "200", description = "Mise à jour")
    public ResponseEntity<Rental> updateMultipart(
            @PathVariable Long id,
            @RequestPart(value = "rental", required = false) String rentalJson,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "surface", required = false) Double surface,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "description", required = false) String description,
            @RequestPart(value = "picture", required = false) MultipartFile picture
    ) {
        try {
            Rental partial = buildRentalFromMultipartPartial(rentalJson, name, surface, price, description);
            String picturePath = null;
            if (picture != null && !picture.isEmpty()) {
                try { picturePath = storageService.savePicture(picture); } catch (Exception ex) { return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); }
            }
            if (partial == null && picturePath == null) {
                return ResponseEntity.badRequest().build();
            }
            return rentalService.updateRental(id, partial != null ? partial : new Rental(), picturePath)
                    .map(r -> {
                        String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
                        if (r.getPicture() != null && !r.getPicture().startsWith("http")) {
                            r.setPicture(base + r.getPicture());
                        }
                        return ResponseEntity.ok(r);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une location")
    @ApiResponse(responseCode = "204", description = "Supprimée")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rentalService.deleteRental(id);
        return ResponseEntity.noContent().build();
    }

    private Rental buildRentalFromMultipartStrict(String rentalJson,
                                                  String name,
                                                  Double surface,
                                                  Double price,
                                                  String description) throws Exception {
        if (rentalJson != null && !rentalJson.isBlank()) {
            return objectMapper.readValue(rentalJson, Rental.class);
        }
        if (name != null && surface != null && price != null && description != null) {
            Rental r = new Rental();
            r.setName(name);
            r.setSurface(surface);
            r.setPrice(price);
            r.setDescription(description);
            return r;
        }
        return null;
    }

    private Rental buildRentalFromMultipartPartial(String rentalJson,
                                                   String name,
                                                   Double surface,
                                                   Double price,
                                                   String description) throws Exception {
        Rental r = new Rental();
        boolean any = false;
        if (rentalJson != null && !rentalJson.isBlank()) {
            Rental parsed = objectMapper.readValue(rentalJson, Rental.class);
            if (parsed.getName() != null) { r.setName(parsed.getName()); any = true; }
            if (parsed.getSurface() != null) { r.setSurface(parsed.getSurface()); any = true; }
            if (parsed.getPrice() != null) { r.setPrice(parsed.getPrice()); any = true; }
            if (parsed.getDescription() != null) { r.setDescription(parsed.getDescription()); any = true; }
        } else {
            if (name != null) { r.setName(name); any = true; }
            if (surface != null) { r.setSurface(surface); any = true; }
            if (price != null) { r.setPrice(price); any = true; }
            if (description != null) { r.setDescription(description); any = true; }
        }
        return any ? r : null;
    }
}
