package com.example.smarthub.controllers;

import com.example.smarthub.enums.Role;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        logger.info("Dashboard accessed by {}", userDetails.getUsername());
        model.addAttribute("username", userDetails.getUsername());
        return "index";
    }

    @GetMapping("/students")
    public String studentsPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Tot user-ul logat
        String email = userDetails.getUsername();
        logger.info("Students page accessed by {}", email);
        User currentUser = userRepository.findByEmail(email).orElseThrow();

        // Verifică dacă e ADMIN
        boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
        model.addAttribute("isAdmin", isAdmin);

        // Trimite toți userii cu rol STUDENT
        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(Role.STUDENT))
                .collect(Collectors.toList());

        model.addAttribute("students", students);

        return "students";
    }
}
