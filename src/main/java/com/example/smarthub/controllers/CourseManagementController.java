package com.example.smarthub.controllers;

import com.example.smarthub.models.Course;
import com.example.smarthub.services.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/courses")
public class CourseManagementController {
    private final CourseService courseService;

    public CourseManagementController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public String listCourses(@RequestParam(value = "keyword", required = false) String keyword,
                              Model model) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("courses", courseService.searchCourses(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("courses", courseService.getAllCourses());
        }
        return "courses";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("course", new Course());
        return "add-course";
    }

    @PostMapping("/add")
    public String addCourse(@ModelAttribute("course") Course course) {
        courseService.createCourse(course);
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        if (course == null) {
            return "redirect:/courses";
        }
        model.addAttribute("course", course);
        return "edit-course";
    }

    @PostMapping("/edit/{id}")
    public String editCourse(@PathVariable Long id, @ModelAttribute("course") Course course) {
        courseService.updateCourse(id, course);
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }

}
