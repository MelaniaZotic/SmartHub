package com.example.smarthub.controllers;

import com.example.smarthub.enums.EnrollmentStatus;
import com.example.smarthub.enums.Role;
import com.example.smarthub.models.Course;
import com.example.smarthub.models.Enrollment;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.GradeRepository;
import com.example.smarthub.repositories.UserRepository;
import com.example.smarthub.services.CourseService;
import com.example.smarthub.services.EnrollmentService;
import com.example.smarthub.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentManagementController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;
    private final CourseService courseService;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentManagementController.class);


    public EnrollmentManagementController(EnrollmentService enrollmentService,
                                          UserService userService,
                                          CourseService courseService, UserRepository userRepository, GradeRepository gradeRepository) {
        this.enrollmentService = enrollmentService;
        this.userService = userService;
        this.courseService = courseService;
        this.userRepository = userRepository;
        this.gradeRepository = gradeRepository;
    }

    @GetMapping("/add")
    public String showAddForm(Model model,
                              @AuthenticationPrincipal UserDetails userDetails) {
        User professor = userService.getUserByEmail(userDetails.getUsername());

        if (professor == null || !professor.getRoles().contains(Role.PROFESSOR)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("enrollment", new Enrollment());
        model.addAttribute("students", userService.getAllStudents());
        model.addAttribute("courses", courseService.getCoursesByProfessorId(professor.getId()));
        model.addAttribute("statuses", EnrollmentStatus.values());
        return "add-enrollment";
    }

    @PostMapping("/add")
    public String addEnrollment(@ModelAttribute("enrollment") Enrollment enrollment,
                                @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Showing add enrollment form");
        User professor = userService.getUserByEmail(userDetails.getUsername());

        if (professor == null || !professor.getRoles().contains(Role.PROFESSOR)) {
            return "redirect:/access-denied";
        }

        // validate if the course really belongs to this professor
        boolean isOwnCourse = courseService.getCoursesByProfessorId(professor.getId())
                .stream()
                .anyMatch(c -> c.getId().equals(enrollment.getCourse().getId()));
        if (!isOwnCourse) {
            return "redirect:/access-denied";
        }

        enrollmentService.createEnrollment(enrollment);
        return "redirect:/enrollments";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(id);
        if (enrollment == null) return "redirect:/enrollments";

        User professor = userService.getUserByEmail(userDetails.getUsername());
        if (professor == null || !professor.getRoles().contains(Role.PROFESSOR)) {
            return "redirect:/access-denied";
        }

        model.addAttribute("enrollment", enrollment);
        model.addAttribute("students", userService.getAllStudents());
        model.addAttribute("courses", courseService.getCoursesByProfessorId(professor.getId()));
        model.addAttribute("statuses", EnrollmentStatus.values());
        return "edit-enrollment";
    }

    @PostMapping("/edit/{id}")
    public String editEnrollment(@PathVariable Long id,
                                 @ModelAttribute("enrollment") Enrollment enrollment,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User professor = userService.getUserByEmail(userDetails.getUsername());

        if (professor == null || !professor.getRoles().contains(Role.PROFESSOR)) {
            return "redirect:/access-denied";
        }

        boolean isOwnCourse = courseService.getCoursesByProfessorId(professor.getId())
                .stream()
                .anyMatch(c -> c.getId().equals(enrollment.getCourse().getId()));
        if (!isOwnCourse) {
            return "redirect:/access-denied";
        }

        enrollmentService.updateEnrollment(id, enrollment);
        return "redirect:/enrollments";
    }

    @GetMapping("/delete/{id}")
    public String deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
        return "redirect:/enrollments";
    }


    @GetMapping
    public String listEnrollments(Model model,
                                  @RequestParam(value = "sort", required = false) String sort,
                                  @RequestParam(value = "courseId", required = false) Long courseId,
                                  @AuthenticationPrincipal UserDetails userDetails) {

        logger.info("Listing enrollments");

        List<Enrollment> enrollments;

        if (courseId != null) {
            logger.info("Filtering enrollments by courseId = {}", courseId);
            enrollments = enrollmentService.getEnrollmentsByCourseId(courseId);
        } else {
            enrollments = enrollmentService.getAllEnrollments();
        }

        if ("course".equalsIgnoreCase(sort)) {
            enrollments.sort(Comparator.comparing(e -> e.getCourse().getTitle(), String.CASE_INSENSITIVE_ORDER));
        } else if ("student".equalsIgnoreCase(sort)) {
            enrollments.sort(Comparator.comparing(e -> e.getStudent().getUsername(), String.CASE_INSENSITIVE_ORDER));
        } else if ("date".equalsIgnoreCase(sort)) {
            enrollments.sort(Comparator.comparing(Enrollment::getEnrollmentDate));
        }

        // ✅ LEAGĂ NOTA (Grade) la Enrollment:
        for (Enrollment en : enrollments) {
            gradeRepository.findByStudentIdAndCourseId(
                    en.getStudent().getId(),
                    en.getCourse().getId()
            ).ifPresent(en::setGrade);
        }

        // 🔑 Populează cursurile profesorului pentru filtrare:
        User loggedUser = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Course> courses = courseService.getCoursesByProfessorId(loggedUser.getId());

        model.addAttribute("enrollments", enrollments);
        model.addAttribute("sort", sort);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courses", courses);

        return "enrollments";
    }


}
