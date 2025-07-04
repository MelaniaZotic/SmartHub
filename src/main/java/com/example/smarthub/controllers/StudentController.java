package com.example.smarthub.controllers;

import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
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
    public String getStudents(Model model, Pageable pageable) {
        int pageSize = 3;
        int currentPage = pageable.getPageNumber();

        List<User> allStudents = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.name().equals("STUDENT")))
                .collect(Collectors.toList());

        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allStudents.size());

        List<User> studentsPage = allStudents.subList(start, end);

        Page<User> page = new PageImpl<>(studentsPage, PageRequest.of(currentPage, pageSize), allStudents.size());

        model.addAttribute("studentsPage", page);

        // Exemplu: presupunem că doar ADMIN poate edita/șterge — setează după cum ai logică
        model.addAttribute("isAdmin", true);

        return "students";
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
