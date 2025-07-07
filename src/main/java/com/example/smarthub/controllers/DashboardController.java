package com.example.smarthub.controllers;

import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Default values
        boolean isProfessor = false;
        boolean isStudent = false;
        boolean isAdmin = false;
        String username = "Utilizator";

        if (userDetails != null) {
            User loggedUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

            if (loggedUser != null) {
                isProfessor = loggedUser.getRoles().stream().anyMatch(r -> r.name().equals("PROFESSOR"));
                isStudent = loggedUser.getRoles().stream().anyMatch(r -> r.name().equals("STUDENT"));
                isAdmin = loggedUser.getRoles().stream().anyMatch(r -> r.name().equals("ADMIN"));
                username = loggedUser.getUsername();
            }
        }

        model.addAttribute("username", username);
        model.addAttribute("isProfessor", isProfessor);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isAdmin", isAdmin);

        return "index";
    }

}
