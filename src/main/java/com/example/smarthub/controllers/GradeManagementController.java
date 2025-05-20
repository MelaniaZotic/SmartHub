package com.example.smarthub.controllers;

import com.example.smarthub.enums.GradeType;
import com.example.smarthub.models.Grade;
import com.example.smarthub.services.CourseService;
import com.example.smarthub.services.GradeService;
import com.example.smarthub.services.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/grades")
public class GradeManagementController {

    private final GradeService gradeService;
    private final StudentService studentService;
    private final CourseService courseService;

    public GradeManagementController(GradeService gradeService,
                                     StudentService studentService,
                                     CourseService courseService) {
        this.gradeService = gradeService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping
    public String listGrades(Model model) {
        model.addAttribute("grades", gradeService.getAllGrades());
        return "grades";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("grade", new Grade());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("types", GradeType.values());
        return "add-grade";
    }

    @PostMapping("/add")
    public String addGrade(@ModelAttribute("grade") Grade grade) {
        gradeService.createGrade(grade);
        return "redirect:/grades";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Grade grade = gradeService.getGradeById(id);
        if (grade == null) return "redirect:/grades";

        model.addAttribute("grade", grade);
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("types", GradeType.values());
        return "edit-grade";
    }

    @PostMapping("/edit/{id}")
    public String editGrade(@PathVariable Long id, @ModelAttribute("grade") Grade grade) {
        gradeService.updateGrade(id, grade);
        return "redirect:/grades";
    }

    @GetMapping("/delete/{id}")
    public String deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
        return "redirect:/grades";
    }

}
