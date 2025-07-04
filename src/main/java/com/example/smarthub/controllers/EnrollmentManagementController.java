//package com.example.smarthub.controllers;
//
//import com.example.smarthub.enums.EnrollmentStatus;
//import com.example.smarthub.models.Enrollment;
//import com.example.smarthub.services.CourseService;
//import com.example.smarthub.services.EnrollmentService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//@Controller
//@RequestMapping("/enrollments")
//public class EnrollmentManagementController {
//
//    private final EnrollmentService enrollmentService;
//    private final StudentService studentService;
//    private final CourseService courseService;
//
//    private static final Logger logger = LoggerFactory.getLogger(EnrollmentManagementController.class);
//
//    public EnrollmentManagementController(EnrollmentService enrollmentService,
//                                          StudentService studentService,
//                                          CourseService courseService) {
//        this.enrollmentService = enrollmentService;
//        this.studentService = studentService;
//        this.courseService = courseService;
//    }
//
//    @GetMapping
//    public String listEnrollments(Model model) {
//        logger.info("Listing all enrollments");
//        model.addAttribute("enrollments", enrollmentService.getAllEnrollments());
//        return "enrollments";
//    }
//
//    @GetMapping("/add")
//    public String showAddForm(Model model) {
//        logger.info("Showing add enrollment form");
//        model.addAttribute("enrollment", new Enrollment());
//        model.addAttribute("students", studentService.getAllStudents());
//        model.addAttribute("courses", courseService.getAllCourses());
//        model.addAttribute("statuses", EnrollmentStatus.values());
//        return "add-enrollment";
//    }
//
//    @PostMapping("/add")
//    public String addEnrollment(@ModelAttribute("enrollment") Enrollment enrollment) {
//        logger.info("Adding new enrollment: {}", enrollment);
//        enrollmentService.createEnrollment(enrollment);
//        return "redirect:/enrollments";
//    }
//
//    @GetMapping("/edit/{id}")
//    public String showEditForm(@PathVariable Long id, Model model) {
//        logger.info("Showing edit form for enrollment ID: {}", id);
//        Enrollment enrollment = enrollmentService.getEnrollmentById(id);
//        if (enrollment == null) {
//            logger.warn("Enrollment with ID {} not found", id);
//            return "redirect:/enrollments";
//        }
//
//        model.addAttribute("enrollment", enrollment);
//        model.addAttribute("students", studentService.getAllStudents());
//        model.addAttribute("courses", courseService.getAllCourses());
//        model.addAttribute("statuses", EnrollmentStatus.values());
//        return "edit-enrollment";
//    }
//
//    @PostMapping("/edit/{id}")
//    public String editEnrollment(@PathVariable Long id, @ModelAttribute("enrollment") Enrollment enrollment) {
//        logger.info("Editing enrollment ID: {}", id);
//        enrollmentService.updateEnrollment(id, enrollment);
//        return "redirect:/enrollments";
//    }
//
//    @GetMapping("/delete/{id}")
//    public String deleteEnrollment(@PathVariable Long id) {
//        logger.info("Deleting enrollment ID: {}", id);
//        enrollmentService.deleteEnrollment(id);
//        return "redirect:/enrollments";
//    }
//
//}
