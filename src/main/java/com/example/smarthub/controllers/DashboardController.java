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

@Controller
public class DashboardController {

    private final UserRepository userRepository;

    public DashboardController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User loggedUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        boolean isProfessor = false;
        boolean isStudent = false;

        if (loggedUser != null) {
            isProfessor = loggedUser.getRoles().stream().anyMatch(r -> r.name().equals("PROFESSOR"));
            isStudent = loggedUser.getRoles().stream().anyMatch(r -> r.name().equals("STUDENT"));
        }

        model.addAttribute("username", loggedUser.getEmail());
        model.addAttribute("isProfessor", isProfessor);
        model.addAttribute("isStudent", isStudent);

        return "index";
    }


}
