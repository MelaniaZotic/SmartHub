package com.example.smarthub.services;

import com.example.smarthub.models.Course;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.CourseRepository;
import com.example.smarthub.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);

    public CourseServiceImpl(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
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
        logger.info("Creating course: {}", course.getTitle());
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

        logger.info("Deleting course id: {}", id);
        courseRepository.deleteById(id);
    }

    @Override
    public List<Course> searchCourses(String keyword) {

        logger.debug("Searching courses with keyword: {}", keyword);
        return courseRepository.searchCourses(keyword);
    }

    /**
     * Returnează User-ul logat (din SecurityContext)
     */
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return userRepository.findByEmail(email).orElse(null);
    }
}
