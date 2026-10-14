
package com.petadoption.pet_adoption_portal;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/adoption-requests")
@CrossOrigin(origins = "http://localhost:5173")
public class AdoptionApiController {

    private final PetRepository petRepository;
    private final AdoptionRequestRepository requestRepository;

    public AdoptionApiController(
            PetRepository petRepository,
            AdoptionRequestRepository requestRepository) {
        this.petRepository = petRepository;
        this.requestRepository = requestRepository;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitRequest(
            @RequestBody AdoptionRequest request) {

        if (request.getPet() == null
                || request.getPet().getId() == null
                || request.getApplicantName() == null
                || request.getApplicantName().isBlank()
                || request.getApplicantEmail() == null
                || request.getApplicantEmail().isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            "Pet, applicant name and email are required."));
        }

        Pet pet = petRepository.findById(
                request.getPet().getId()).orElse(null);

        if (pet == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Pet not found."));
        }

        if (!"Available".equalsIgnoreCase(pet.getStatus())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    Map.of("message",
                            "This pet is no longer available."));
        }

        request.setPet(pet);
        request.setStatus("Pending");

        AdoptionRequest savedRequest = requestRepository.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                Map.of(
                        "message", "Adoption request submitted successfully!",
                        "requestId", savedRequest.getId(),
                        "status", savedRequest.getStatus()
                ));
    }
}
