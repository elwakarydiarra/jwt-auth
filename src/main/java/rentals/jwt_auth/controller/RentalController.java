package rentals.jwt_auth.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import rentals.jwt_auth.model.Rental;
import rentals.jwt_auth.model.User;
import rentals.jwt_auth.repository.UserRepository;
import rentals.jwt_auth.service.RentalService;
import org.springframework.web.multipart.MultipartFile;


import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@Tag(name = "Rentals", description = "CRUD des locations et upload d’images")
public class RentalController {

    private final RentalService rentalService;
    private final UserRepository userRepository;

    public RentalController(RentalService rentalService, UserRepository userRepository) {
        this.rentalService = rentalService;
        this.userRepository = userRepository;
    }

    private String baseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }

   
    private String toAbsoluteUrl(String base, String maybePath) {
        if (maybePath == null || maybePath.isBlank()) return maybePath;
        String lower = maybePath.toLowerCase();
        if (lower.startsWith("http://") || lower.startsWith("https://")) return maybePath;
        String path = maybePath.startsWith("/") ? maybePath : "/" + maybePath;
        return base + path;
    }

    
    private String savePictureToUploads(MultipartFile picture) throws IOException {
        String uploadDir = new File("uploads").getAbsolutePath();
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        String filename = System.currentTimeMillis() + "_" + picture.getOriginalFilename();
        File filePath = new File(uploadDirFile, filename);
        picture.transferTo(filePath);
        return "/uploads/" + filename;
    }

    @Operation(
            summary = "Lister toutes les locations",
            description = "Renvoie la liste des locations. La propriété `picture` est renvoyée en URL absolue.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste renvoyée"),
                    @ApiResponse(responseCode = "401", description = "Non authentifié")
                })
    
    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals(HttpServletRequest request) {
        String base = baseUrl(request);
        List<Rental> rentals = rentalService.getAllRentals();
        rentals.forEach(r -> r.setPicture(toAbsoluteUrl(base, r.getPicture())));
        return ResponseEntity.ok(rentals);
    }
    
    @Operation(
            summary = "Détails d’une location",
            description = "Renvoie une location par son identifiant. La propriété `picture` est renvoyée en URL absolue.",
            responses = {
                    	@ApiResponse(responseCode = "200", description = "Location trouvée"),
                        @ApiResponse(responseCode = "404", description = "Location introuvable"),
                        @ApiResponse(responseCode = "401", description = "Non authentifié")
        })

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRental(@PathVariable Long id, HttpServletRequest request) {
        String base = baseUrl(request);
        return rentalService.getRentalById(id)
                .map(r -> {
                    r.setPicture(toAbsoluteUrl(base, r.getPicture()));
                    return ResponseEntity.ok(r);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(
            summary = "Créer une location (JSON)",
            description = "Crée une location à partir d’un JSON (sans upload). `picture` peut être une URL relative (ex: `/uploads/xxx.jpg`) ou absolue.",
            responses = {
                    	@ApiResponse(responseCode = "200", description = "Créée"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "401", description = "Non authentifié")
        })

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Rental> createRentalJson(@RequestBody Rental rental, HttpServletRequest request) {
        Rental saved = rentalService.createRental(rental);
        saved.setPicture(toAbsoluteUrl(baseUrl(request), saved.getPicture()));
        return ResponseEntity.ok(saved);
    }
    
    @Operation(
            summary = "Créer une location (multipart + image)",
            description = "Crée une location en envoyant les champs + une image. L’owner est déduit de l’utilisateur authentifié.",
            responses = {
                    	@ApiResponse(responseCode = "200", description = "Créée"),
                        @ApiResponse(responseCode = "400", description = "Données invalides"),
                        @ApiResponse(responseCode = "401", description = "Non authentifié")
        })

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Rental> createRental(
    		@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            HttpServletRequest request
    ) {
    	User owner = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    	
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);
        rental.setOwner(owner);

        try {
            if (picture != null && !picture.isEmpty()) {
                String relativePath = savePictureToUploads(picture); 
                rental.setPicture(relativePath);                     
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du téléchargement de l'image");
        }

        Rental saved = rentalService.createRental(rental);
        saved.setPicture(toAbsoluteUrl(baseUrl(request), saved.getPicture()));
        return ResponseEntity.ok(saved);
    }
    
    @Operation(
            summary = "Mettre à jour une location (multipart)",
            description = "Met à jour les champs, et remplace l’image si fournie.",
            responses = {
                    	@ApiResponse(responseCode = "200", description = "Mise à jour effectuée"),
                        @ApiResponse(responseCode = "404", description = "Location introuvable"),
                        @ApiResponse(responseCode = "401", description = "Non authentifié")
        })

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Rental> updateRental(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            HttpServletRequest request
    ) {
        String base = baseUrl(request);

        return rentalService.getRentalById(id).map(existing -> {
            existing.setName(name);
            existing.setSurface(surface);
            existing.setPrice(price);
            existing.setDescription(description);

            try {
                if (picture != null && !picture.isEmpty()) {
                    String relativePath = savePictureToUploads(picture);
                    existing.setPicture(relativePath);                   
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Erreur lors du téléchargement de l'image");
            }

            Rental updated = rentalService.createRental(existing);
            updated.setPicture(toAbsoluteUrl(base, updated.getPicture()));
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
}
