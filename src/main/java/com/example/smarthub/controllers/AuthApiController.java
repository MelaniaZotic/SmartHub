package com.example.smarthub.controllers;

import com.example.smarthub.enums.Role;
import com.example.smarthub.models.User;
import com.example.smarthub.models.dtos.RegisterRequest;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/auth")
public class AuthApiController {

    private final PasswordEncoder encoder;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(AuthApiController.class);
    public AuthApiController(PasswordEncoder encoder, UserRepository userRepository) {
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        logger.info("Register endpoint called for email: {}", registerRequest.getEmail());
        String email = registerRequest.getEmail().trim().toLowerCase();
        String username = registerRequest.getUsername().trim();

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Utilizatorul există deja.");
        }

        Role role;
        if (email.endsWith("@s.unibuc.ro")) {
            role = Role.STUDENT;
        } else if (email.endsWith("@prof.unibuc.ro")) {
            role = Role.PROFESSOR;
        } else {
            logger.warn("Registration bad request for email: {}", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Emailul trebuie să fie instituțional: @s.unibuc.ro sau @prof.unibuc.ro");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);
        user.setUsername(username); // 🔑 Salvăm username-ul!
        user.getRoles().add(role);

        userRepository.save(user);
        logger.info("User registered successfully with role {}", role.name());
        return ResponseEntity.ok("Înregistrare reușită cu rol: " + role.name());
    }

}
