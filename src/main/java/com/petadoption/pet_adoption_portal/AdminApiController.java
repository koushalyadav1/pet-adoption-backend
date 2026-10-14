
package com.petadoption.pet_adoption_portal;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/requests")
public class AdminApiController {

    private final AdoptionRequestRepository requestRepository;

    public AdminApiController(
            AdoptionRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @GetMapping
    public List<AdoptionRequest> getAllRequests() {
        return requestRepository.findAll();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");

        if (!"Approved".equals(status)
                && !"Rejected".equals(status)) {
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            "Status must be Approved or Rejected."));
        }

        AdoptionRequest request =
                requestRepository.findById(id).orElse(null);

        if (request == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message", "Adoption request not found."));
        }

        request.setStatus(status);
        requestRepository.save(request);

        return ResponseEntity.ok(
                Map.of("message", "Request updated.", "status", status));
    }
}
