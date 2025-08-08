package rentals.jwt_auth.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import rentals.jwt_auth.model.Rental;
import rentals.jwt_auth.service.RentalService;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
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

    
    @GetMapping
    public ResponseEntity<List<Rental>> getAllRentals(HttpServletRequest request) {
        String base = baseUrl(request);
        List<Rental> rentals = rentalService.getAllRentals();
        rentals.forEach(r -> r.setPicture(toAbsoluteUrl(base, r.getPicture())));
        return ResponseEntity.ok(rentals);
    }

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

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Rental> createRentalJson(@RequestBody Rental rental, HttpServletRequest request) {
        Rental saved = rentalService.createRental(rental);
        saved.setPicture(toAbsoluteUrl(baseUrl(request), saved.getPicture()));
        return ResponseEntity.ok(saved);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Rental> createRental(
            @RequestParam("name") String name,
            @RequestParam("surface") Double surface,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam(value = "picture", required = false) MultipartFile picture,
            HttpServletRequest request
    ) {
        Rental rental = new Rental();
        rental.setName(name);
        rental.setSurface(surface);
        rental.setPrice(price);
        rental.setDescription(description);

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
