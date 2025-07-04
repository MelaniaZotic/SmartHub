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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    public StudentController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String getStudents(Model model, Pageable pageable) {
        logger.info("Fetching students page: {}", pageable.getPageNumber());
        int pageSize = 3;
        int currentPage = pageable.getPageNumber();

        List<User> allStudents = userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.name().equals("STUDENT")))
                .collect(Collectors.toList());

        int start = currentPage * pageSize;
        int end = Math.min(start + pageSize, allStudents.size());

        logger.debug("Calculated start: {}, end: {}", start, end);

        List<User> studentsPage = allStudents.subList(start, end);

        Page<User> page = new PageImpl<>(studentsPage, PageRequest.of(currentPage, pageSize), allStudents.size());

        model.addAttribute("studentsPage", page);


        model.addAttribute("isAdmin", true);

        logger.info("Returning students view with {} students on this page", studentsPage.size());

        return "students";
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {

        logger.warn("Deleting student with id: {}", id);
        userRepository.deleteById(id);
    }
}
