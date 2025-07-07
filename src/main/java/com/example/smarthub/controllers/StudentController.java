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

        User loggedUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

        boolean isAdmin = false;
        boolean isProfessor = false;
        boolean isStudent = false;

        if (loggedUser != null) {
            isAdmin = loggedUser.getRoles().stream().anyMatch(role -> role.name().equals("ADMIN"));
            isProfessor = loggedUser.getRoles().stream().anyMatch(role -> role.name().equals("PROFESSOR"));
            isStudent = loggedUser.getRoles().stream().anyMatch(role -> role.name().equals("STUDENT"));
        }

        // Decide ce listezi
        List<User> allUsers;
        if (isStudent) {
            // Student vede doar profesori
            allUsers = userRepository.findAll().stream()
                    .filter(user -> user.getRoles().stream().anyMatch(r -> r.name().equals("PROFESSOR")))
                    .collect(Collectors.toList());
        } else {
            // Profesor/Admin vede doar studenți
            allUsers = userRepository.findAll().stream()
                    .filter(user -> user.getRoles().stream().anyMatch(r -> r.name().equals("STUDENT")))
                    .collect(Collectors.toList());
        }

        int start = page * pageSize;
        int end = Math.min(start + pageSize, allUsers.size());
        List<User> usersPage = allUsers.subList(start, end);

        Page<User> pageObj = new PageImpl<>(usersPage, PageRequest.of(page, pageSize), allUsers.size());

        model.addAttribute("studentsPage", pageObj);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isProfessor", isProfessor);

        return "students";
    }

}
