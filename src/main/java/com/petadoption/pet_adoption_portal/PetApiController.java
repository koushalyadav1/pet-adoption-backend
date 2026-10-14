package com.petadoption.pet_adoption_portal;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pets")
public class PetApiController {

    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final AdoptionRequestRepository adoptionRequestRepository;

    public PetApiController(
            PetRepository petRepository,
            UserRepository userRepository,
            AdoptionRequestRepository adoptionRequestRepository) {
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.adoptionRequestRepository = adoptionRequestRepository;
    }

    @GetMapping
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePet(
            @PathVariable Long id,
            Authentication authentication) {

        // Check login
        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Please log in first."));
        }

        // Find the pet
        Pet pet = petRepository.findById(id).orElse(null);

        if (pet == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Pet not found."));
        }

        // Check admin role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                        || authority.getAuthority().equals("ADMIN"));

        // Verify ownership for regular users
        if (!isAdmin) {
            User currentUser = userRepository
                    .findByEmail(authentication.getName())
                    .orElse(null);

            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "message",
                                "Your account could not be verified."
                        ));
            }

            User owner = pet.getOwner();

            if (owner == null
                    || owner.getId() == null
                    || !owner.getId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(
                                "message",
                                "You can delete only pets you uploaded."
                        ));
            }
        }

        // Permanently delete adoption requests linked to this pet
        adoptionRequestRepository.deleteByPet_Id(id);

        // Permanently delete the pet
        petRepository.delete(pet);

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "Pet and associated adoption requests deleted successfully."
                )
        );
    }
}
