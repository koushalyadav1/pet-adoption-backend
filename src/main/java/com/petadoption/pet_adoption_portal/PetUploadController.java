
package com.petadoption.pet_adoption_portal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pets")
public class PetUploadController {

    private final PetRepository petRepository;
    private final UserRepository userRepository;

    private static final Path UPLOAD_DIR =
            Paths.get("uploads", "pets").toAbsolutePath().normalize();

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp");

    public PetUploadController(
            PetRepository petRepository,
            UserRepository userRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPet(
            @RequestParam String name,
            @RequestParam String species,
            @RequestParam(required = false, defaultValue = "") String breed,
            @RequestParam(defaultValue = "0") int age,
            @RequestParam(required = false, defaultValue = "") String description,
            @RequestParam MultipartFile photo,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Please log in before uploading a pet."));
        }

        User owner = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (owner == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Your account could not be verified."));
        }

        if (name.isBlank() || species.isBlank() || age < 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Enter a pet name, species, and valid age."));
        }

        if (photo == null || photo.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Please select a pet photo."));
        }

        if (photo.getSize() > 5 * 1024 * 1024) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Photo must be 5 MB or smaller."));
        }

        String contentType = photo.getContentType();

        if (contentType == null ||
                !ALLOWED_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Use a JPG, PNG, or WEBP photo."));
        }

        String extension = switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new IllegalStateException("Unsupported image type");
        };

        String filename = UUID.randomUUID() + extension;
        Path destination = UPLOAD_DIR.resolve(filename).normalize();

        try {
            Files.createDirectories(UPLOAD_DIR);
            photo.transferTo(destination);

            Pet pet = new Pet();
            pet.setName(name.trim());
            pet.setSpecies(species.trim());
            pet.setBreed(breed.trim());
            pet.setAge(age);
            pet.setDescription(description.trim());
            pet.setImageUrl("/uploaded-pets/" + filename);
            pet.setStatus("Available");
            pet.setOwner(owner);

            Pet saved = petRepository.save(pet);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Pet uploaded successfully!",
                    "id", saved.getId(),
                    "name", saved.getName(),
                    "imageUrl", saved.getImageUrl()
            ));

        } catch (IOException ex) {
            return ResponseEntity.internalServerError().body(
                    Map.of("message", "Could not save the photo. Please try again."));
        }
    }
}
