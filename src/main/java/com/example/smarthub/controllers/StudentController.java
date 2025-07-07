package com.example.smarthub.controllers;

import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StudentController {

    private final UserRepository userRepository;

    public StudentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/students")
    public String getStudents(Model model,
                              @RequestParam(defaultValue = "0") int page,
                              @AuthenticationPrincipal UserDetails userDetails) {

        int pageSize = 3;

        List<User> allStudents = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.name().equals("STUDENT")))
                .collect(Collectors.toList());

        int start = page * pageSize;
        int end = Math.min(start + pageSize, allStudents.size());

        List<User> studentsPage = allStudents.subList(start, end);

        Page<User> pageObj = new PageImpl<>(
                studentsPage,
                PageRequest.of(page, pageSize),
                allStudents.size()
        );

        // VERIFICĂ DACĂ USERUL LOGAT E ADMIN
        boolean isAdmin = false;
        if (userDetails != null) {
            User loggedUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (loggedUser != null) {
                isAdmin = loggedUser.getRoles().stream()
                        .anyMatch(role -> role.name().equals("ADMIN"));
            }
        }

        model.addAttribute("studentsPage", pageObj);
        model.addAttribute("isAdmin", isAdmin);

        return "students";
    }
}
