
package com.petadoption.pet_adoption_portal;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "No account found for email: " + email
                    )
                );

        String role = user.getRole();

        if (role == null || role.isBlank()) {
            role = "USER";
        }

        // Accept either USER / ADMIN or ROLE_USER / ROLE_ADMIN
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(role)
                .build();
    }
}
