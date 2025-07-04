package com.example.smarthub.controllers;

import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final UserRepository userRepository;

    public StudentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getStudents() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.name().equals("STUDENT")))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
