
package com.petadoption.pet_adoption_portal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PetController {

    private final PetRepository petRepository;

    public PetController(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @GetMapping("/pets")
    public String showPets(Model model) {
        model.addAttribute(
            "pets",
            petRepository.findByStatus("Available")
        );
        return "pets";
    }

    @GetMapping("/admin/pets/new")
    public String showAddPetForm() {
        return "add-pet";
    }

    @PostMapping("/admin/pets/add")
    public String addPet(
            @RequestParam String name,
            @RequestParam String species,
            @RequestParam String breed,
            @RequestParam int age,
            @RequestParam String description,
            @RequestParam("image") MultipartFile image,
            RedirectAttributes redirectAttributes) {

        if (name.isBlank() || species.isBlank()
                || breed.isBlank() || age < 0) {
            redirectAttributes.addFlashAttribute(
                "error", "Please enter valid pet details."
            );
            return "redirect:/admin/pets/new";
        }

        if (image.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                "error", "Please select a pet photo."
            );
            return "redirect:/admin/pets/new";
        }

        Map<String, String> allowedTypes = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
        );

        String extension = allowedTypes.get(image.getContentType());

        if (extension == null) {
            redirectAttributes.addFlashAttribute(
                "error", "Upload a JPG, PNG, or WEBP image."
            );
            return "redirect:/admin/pets/new";
        }

        try {
            Path uploadDir = Path.of(
                "src", "main", "resources",
                "static", "images", "pets"
            ).toAbsolutePath().normalize();

            Files.createDirectories(uploadDir);

            String filename = UUID.randomUUID() + extension;
            Path destination = uploadDir.resolve(filename).normalize();

            if (!destination.startsWith(uploadDir)) {
                throw new IOException("Invalid upload path.");
            }

            Files.copy(
                image.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
            );

            Pet pet = new Pet();
            pet.setName(name.trim());
            pet.setSpecies(species.trim());
            pet.setBreed(breed.trim());
            pet.setAge(age);
            pet.setDescription(description.trim());
            pet.setImageUrl("/images/pets/" + filename);
            pet.setStatus("Available");

            petRepository.save(pet);

            redirectAttributes.addFlashAttribute(
                "success", "Pet added successfully!"
            );
            return "redirect:/pets";

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute(
                "error", "Could not save the photo. Please try again."
            );
            return "redirect:/admin/pets/new";
        }
    }
}
