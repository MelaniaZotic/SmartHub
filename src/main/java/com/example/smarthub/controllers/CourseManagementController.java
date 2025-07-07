package com.example.smarthub.controllers;

import com.example.smarthub.models.Course;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import com.example.smarthub.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

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
                .orElseThrow();

        logger.info("User {} accessed /courses", user.getEmail());

        model.addAttribute("currentUserId", user.getId());

        // Verificăm rolurile
        boolean isStudent = user.getRoles().stream().anyMatch(r -> r.name().equals("STUDENT"));
        boolean isProfessor = user.getRoles().stream().anyMatch(r -> r.name().equals("PROFESSOR"));

        model.addAttribute("isStudent", isStudent);
        model.addAttribute("isProfessor", isProfessor);

        if (isStudent) {
            logger.info("Role detected: STUDENT - loading only student's enrolled courses");
            model.addAttribute("courses", courseService.getCoursesByStudentId(user.getId()));
        } else if (isProfessor) {
            logger.info("Role detected: PROFESSOR - loading only professor's taught courses");
            model.addAttribute("courses", courseService.getCoursesByProfessorId(user.getId()));
        } else {
            logger.info("Role detected: OTHER/ADMIN - loading all courses");
            if (keyword != null && !keyword.isBlank()) {
                logger.debug("Searching courses with keyword: {}", keyword);
                model.addAttribute("courses", courseService.searchCourses(keyword));
                model.addAttribute("keyword", keyword);
            } else {
                model.addAttribute("courses", courseService.getAllCourses());
            }
        }

        return "courses";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        logger.info("Accessing add-course form");
        model.addAttribute("course", new Course());
        return "add-course";
    }

    @PostMapping("/add")
    public String addCourse(@ModelAttribute("course") Course course,
                            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        logger.info("User {} is adding a new course", user.getEmail());
        course.setUser(user);
        courseService.createCourse(course);
        return "redirect:/courses";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        logger.info("Accessing edit form for course id {}", id);
        Course course = courseService.getCourseById(id);
        if (course == null) {
            logger.warn("Course with id {} not found", id);
            return "redirect:/courses";
        }
        model.addAttribute("course", course);
        return "edit-course";
    }

    @PostMapping("/edit/{id}")
    public String editCourse(@PathVariable Long id, @ModelAttribute("course") Course course) {
        logger.info("Editing course id {}", id);
        courseService.updateCourse(id, course);
        return "redirect:/courses";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        logger.info("Deleting course id {}", id);
        courseService.deleteCourse(id);
        return "redirect:/courses";
    }
}
