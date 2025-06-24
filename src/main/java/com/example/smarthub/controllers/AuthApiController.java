package com.example.smarthub.controllers;

import com.example.smarthub.config.secret.JwtUtil;
import com.example.smarthub.enums.Role;
import com.example.smarthub.models.User;
import com.example.smarthub.models.dtos.AuthRequest;
import com.example.smarthub.models.dtos.AuthResponse;
import com.example.smarthub.models.dtos.RegisterRequest;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthApiController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder encoder;
    private final UserRepository userRepository;


    public AuthApiController(AuthenticationManager authenticationManager,
                             JwtUtil jwtUtil,
                             UserDetailsService userDetailsService, PasswordEncoder encoder, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(), authRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername());

        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        String email = registerRequest.getEmail().trim().toLowerCase();

        // verificăm dacă emailul există deja
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Utilizatorul există deja.");
        }

        // determinăm rolul automat în funcție de structura emailului
        Role role;
        if (email.matches(".*@s\\..+\\.ro$")) {
            role = Role.STUDENT;
        } else if (email.matches(".*@.+\\.ro$")) {
            role = Role.PROFESSOR;
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Format de email invalid pentru înregistrare.");
        }

        // creăm și salvăm utilizatorul
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);
        user.setRoles(Set.of(role));

        userRepository.save(user);

        return ResponseEntity.ok("Înregistrare reușită cu rol: " + role.name());
    }


}


//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestBody AuthRequest authRequest) {
//
//        // 1. verificăm dacă utilizatorul există deja
//        if (userRepository.existsByEmail(authRequest.getEmail())) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("Utilizatorul există deja.");
//        }
//
//        // 2. creăm și salvăm utilizatorul
//        User user = new User();
//        user.setEmail(authRequest.getEmail());
//        user.setPassword(encoder.encode(authRequest.getPassword()));
//        user.setEmail(authRequest.getEmail());
//        //user.setRoles(Role.USER);
//        userRepository.save(user);
//
//        // 3. întoarcem 200 OK; JS-ul din pagina de înregistrare va face redirect
//        return ResponseEntity.ok("Înregistrare reușită");
//    }



