// Updated StudentManagementController.java
package com.example.smarthub.controllers;

import com.example.smarthub.models.Student;
import com.example.smarthub.services.StudentService;
import com.example.smarthub.services.StudentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/students")
public class StudentManagementController {

    @Autowired
    StudentServiceImpl studentService;


    @GetMapping
    public String listStudents(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {

        int pageSize = 20;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Student> studentPage;

        if (keyword != null && !keyword.isBlank()) {
            studentPage = studentService.searchStudents(keyword, pageable);
        } else {
            studentPage = studentService.findAllPaged(pageable);
        }

        model.addAttribute("page", studentPage);
        model.addAttribute("keyword", keyword);

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
