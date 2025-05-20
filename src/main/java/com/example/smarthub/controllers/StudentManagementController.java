package com.example.smarthub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.smarthub.models.Student;
import com.example.smarthub.services.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentManagementController {

    private final StudentService studentService;

    public StudentManagementController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public String listStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "students";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());
        return "add-student";
    }

    @PostMapping("/add")
    public String addStudent(@ModelAttribute("student") Student student) {
        studentService.createStudent(student);
        return "redirect:/students";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Student student = studentService.getStudentById(id);
        if (student == null) {
            return "redirect:/students";
        }
        model.addAttribute("student", student);
        return "edit-student";
    }

    @PostMapping("/edit/{id}")
    public String editStudent(@PathVariable Long id, @ModelAttribute("student") Student student) {
        studentService.updateStudent(id, student);
        return "redirect:/students";
    }

    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }

}
