package com.example.smarthub.controllers;

import com.example.smarthub.models.Course;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import com.example.smarthub.services.CourseService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/courses")
public class CourseManagementController {
    private static final Logger logger = LoggerFactory.getLogger(CourseManagementController.class);
    private final CourseService courseService;
    private final UserRepository userRepository;
    public CourseManagementController(CourseService courseService, UserRepository userRepository) {
        this.courseService = courseService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listCourses(@RequestParam(value = "keyword", required = false) String keyword,
                              Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(); // ai nevoie de userRepository injectat!

        logger.info("Listing courses for user: {}", userDetails.getUsername());
        model.addAttribute("currentUserId", user.getId());

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
        logger.debug("Showing add-course form");
        model.addAttribute("course", new Course());
        return "add-course";
    }

    @PostMapping("/add")
    public String addCourse(@ModelAttribute("course") Course course,
                            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Adding new course by user: {}", userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        course.setUser(user);
        courseService.createCourse(course);
        return "redirect:/courses";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.debug("Showing edit form for course id: {}", id);
        Course course = courseService.getCourseById(id);
        if (course == null) {
            logger.warn("Course not found for id: {}", id);
            return "redirect:/courses";
        }
        model.addAttribute("course", course);
        return "edit-course";
    }

    @PostMapping("/edit/{id}")
    public String editCourse(@PathVariable Long id, @ModelAttribute("course") Course course) {
        logger.info("Editing course id: {}", id);
        courseService.updateCourse(id, course);
        return "redirect:/courses";
    }


    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        logger.info("Deleting course id: {}", id);
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }

}
