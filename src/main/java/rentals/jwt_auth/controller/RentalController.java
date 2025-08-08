package rentals.jwt_auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rentals.jwt_auth.model.Rental;
import rentals.jwt_auth.service.RentalService;

import java.io.File;
import java.io.IOException;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Operation(summary = "Liste toutes les locations", responses = {
            @ApiResponse(responseCode = "200", description = "Liste des locations")
        })
    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals() {
        return ResponseEntity.ok(rentalService.getAllRentals());
    }

    @Operation(summary = "Ajouter une location", responses = {
            @ApiResponse(responseCode = "200", description = "Location créée")
        })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Rental> createRentalJson(@RequestBody Rental rental) {
        Rental saved = rentalService.createRental(rental);
        return ResponseEntity.ok(saved);
    }
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Rental> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture
    ) {
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);

        if (picture != null && !picture.isEmpty()) {
        	String uploadDir = new File("uploads").getAbsolutePath();
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                uploadDirFile.mkdirs();
            }
            String filename = System.currentTimeMillis() + "_" + picture.getOriginalFilename();
            File filePath = new File(uploadDirFile, filename);


            try {
                picture.transferTo(filePath);
                rental.setPicture("/uploads/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
                
                throw new RuntimeException("Erreur lors du téléchargement de l'image");
            }
        }


        
        Rental saved = rentalService.createRental(rental);
        return ResponseEntity.ok(saved);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Rental> getRental(@PathVariable Long id) {
        return rentalService.getRentalById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Rental> updateRental(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture
    ) {
        return rentalService.getRentalById(id).map(existingRental -> {
            existingRental.setName(name);
            existingRental.setSurface(surface);
            existingRental.setPrice(price);
            existingRental.setDescription(description);

            if (picture != null && !picture.isEmpty()) {
                String uploadDir = new File("uploads").getAbsolutePath();
                File uploadDirFile = new File(uploadDir);
                if (!uploadDirFile.exists()) {
                    uploadDirFile.mkdirs();
                }
                String filename = System.currentTimeMillis() + "_" + picture.getOriginalFilename();
                File filePath = new File(uploadDirFile, filename);
                try {
                    picture.transferTo(filePath);
                    existingRental.setPicture("/uploads/" + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Erreur lors du téléchargement de l'image");
                }
            }

            Rental updated = rentalService.createRental(existingRental); // ou save
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }



}
