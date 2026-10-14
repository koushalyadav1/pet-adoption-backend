
package com.petadoption.pet_adoption_portal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model,
            RedirectAttributes redirectAttributes) {

        name = name.trim();
        email = email.trim().toLowerCase();

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            model.addAttribute("error", "All fields are required.");
            return "register";
        }

        if (password.length() < 8) {
            model.addAttribute(
                "error", "Password must contain at least 8 characters."
            );
            return "register";
        }

        if (userRepository.existsByEmail(email)) {
            model.addAttribute(
                "error", "This email is already registered."
            );
            return "register";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
            "success", "Registration successful! Please log in."
        );

        return "redirect:/login";
    }
}
