
package com.petadoption.pet_adoption_portal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdoptionController {

    private final PetRepository petRepository;
    private final AdoptionRequestRepository requestRepository;

    public AdoptionController(
            PetRepository petRepository,
            AdoptionRequestRepository requestRepository) {
        this.petRepository = petRepository;
        this.requestRepository = requestRepository;
    }

    @GetMapping("/adopt/{id}")
    public String showForm(@PathVariable Long id, Model model) {
        Pet pet = petRepository.findById(id).orElse(null);

        if (pet == null ||
                !"Available".equalsIgnoreCase(pet.getStatus())) {
            return "redirect:/pets";
        }

        model.addAttribute("pet", pet);
        model.addAttribute("request", new AdoptionRequest());

        return "adopt";
    }

    @PostMapping("/adopt/{id}")
    public String submitRequest(
            @PathVariable Long id,
            @ModelAttribute("request") AdoptionRequest request,
            RedirectAttributes redirectAttributes) {

        Pet pet = petRepository.findById(id).orElse(null);

        if (pet == null ||
                !"Available".equalsIgnoreCase(pet.getStatus())) {
            return "redirect:/pets";
        }

        request.setPet(pet);
        request.setStatus("Pending");
        requestRepository.save(request);

        redirectAttributes.addFlashAttribute(
                "success",
                "Your adoption request has been submitted!");

        return "redirect:/pets";
    }
}
