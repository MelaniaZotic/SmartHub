package com.example.smarthub.controllers;

import com.example.smarthub.enums.GradeType;
import com.example.smarthub.models.Course;
import com.example.smarthub.models.Grade;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.CourseRepository;
import com.example.smarthub.repositories.GradeRepository;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/grades")
public class GradeManagementController {


    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public GradeManagementController(GradeRepository gradeRepository, UserRepository userRepository, CourseRepository courseRepository) {
        this.gradeRepository = gradeRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }



    @PostMapping("/add")
    public String addGrade(@RequestParam Long studentId,
                           @RequestParam Long courseId,
                           @RequestParam double value) {

        User student = userRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setCourse(course);
        grade.setValue(value);
        grade.setDate(LocalDate.now());
        grade.setType(GradeType.FINAL);

        gradeRepository.save(grade);

        return "redirect:/enrollments";
    }

    @PostMapping("/edit/{id}")
    public String editGrade(@PathVariable Long id,
                            @RequestParam double value) {

        Grade grade = gradeRepository.findById(id).orElseThrow();
        grade.setValue(value);
        gradeRepository.save(grade);

        return "redirect:/enrollments";
    }


    @GetMapping
    public String listGrades(@AuthenticationPrincipal UserDetails userDetails,
                             Model model) {

        User loggedUser = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow();

        List<Grade> grades;

        if (loggedUser.getRoles().stream().anyMatch(r -> r.name().equals("STUDENT"))) {
            grades = gradeRepository.findByStudentId(loggedUser.getId());
        } else {
            // Altfel — arată toate notele
            grades = gradeRepository.findAll();
        }

        model.addAttribute("grades", grades);

        return "grades";
    }

}
