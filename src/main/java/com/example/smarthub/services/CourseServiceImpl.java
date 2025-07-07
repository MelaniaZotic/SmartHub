package com.example.smarthub.services;

import com.example.smarthub.models.Course;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.CourseRepository;
import com.example.smarthub.repositories.EnrollmentRepository;
import com.example.smarthub.repositories.UserRepository;
import com.example.smarthub.services.CourseService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.smarthub.models.Enrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    private final EnrollmentRepository enrollmentRepository;

    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<Course> getAllCourses() {
        logger.debug("Fetching all courses");
        return courseRepository.findAll();
    }

    @Override
    public Course getCourseById(Long id) {
        logger.debug("Fetching course by id: {}", id);
        return courseRepository.findById(id).orElse(null);
    }

    @Override
    public Course createCourse(Course course) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            logger.error("No authenticated user found!");

            throw new RuntimeException("User not authenticated");
        }
        course.setUser(currentUser);
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Long id, Course course) {
        logger.info("Updating course id: {}", id);

        return courseRepository.findById(id)
                .map(existing -> {
                    existing.setTitle(course.getTitle());
                    existing.setDescription(course.getDescription());
                    existing.setCredits(course.getCredits());
                    existing.setSemester(course.getSemester());
                    // user-ul nu se schimbă!
                    return courseRepository.save(existing);
                }).orElse(null);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword);
    }

    /**
     * Returnează User-ul logat (din SecurityContext)
     */
    private User getCurrentUser() {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            return null;
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email).orElse(null);
    }

    public List<Course> getCoursesByProfessorId(Long professorId) {
        return courseRepository.findAll()
                .stream()
                .filter(c -> c.getUser().getId().equals(professorId))
                .toList();
    }

    @Override
    public List<Course> getCoursesByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream()
                .map(Enrollment::getCourse)
                .distinct()
                .collect(Collectors.toList());
    }

}
