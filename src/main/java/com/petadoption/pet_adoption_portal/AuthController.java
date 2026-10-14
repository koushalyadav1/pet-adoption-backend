
package com.petadoption.pet_adoption_portal;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> data) {
        String name = data.getOrDefault("name", "").trim();
        String email = data.getOrDefault("email", "").trim().toLowerCase();
        String password = data.getOrDefault("password", "");

        if (name.isBlank() || email.isBlank() || password.length() < 8) {
            return ResponseEntity.badRequest().body(
                Map.of("message", "Enter your name, email, and a password of at least 8 characters.")
            );
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                Map.of("message", "An account with this email already exists.")
            );
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            Map.of("message", "Registration successful. Please log in.")
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> data,
            HttpServletRequest request) {

        String email = data.getOrDefault("email", "").trim().toLowerCase();
        String password = data.getOrDefault("password", "");

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
            );

            return ResponseEntity.ok(Map.of("message", "Login successful."));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of("message", "Invalid email or password.")
            );
        }
    }

    
@GetMapping("/me")
public ResponseEntity<?> me(Authentication authentication) {
    if (authentication == null
            || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getName())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    User user = userRepository
            .findByEmail(authentication.getName())
            .orElse(null);

    if (user == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    return ResponseEntity.ok(Map.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail(),
            "role", user.getRole()
    ));
}

}
