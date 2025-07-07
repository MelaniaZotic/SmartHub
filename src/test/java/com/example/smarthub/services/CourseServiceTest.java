package com.example.smarthub.services;

import com.example.smarthub.models.Course;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.CourseRepository;
import com.example.smarthub.repositories.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllCourses_shouldReturnList() {
        List<Course> courses = List.of(new Course(), new Course());
        when(courseRepository.findAll()).thenReturn(courses);

        List<Course> result = courseService.getAllCourses();

        assertThat(result)
                .as("Expected 2 courses, but got a different size")
                .hasSize(2);
        verify(courseRepository).findAll();
    }

    @Test
    void getCourseById_shouldReturnCourse() {
        Course course = new Course();
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.getCourseById(1L);

        assertThat(result)
                .as("Course should not be null")
                .isNotNull();
        assertThat(result.getId())
                .as("Course ID should be 1")
                .isEqualTo(1L);
    }

    @Test
    void getCourseById_notFound_shouldReturnNull() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        Course result = courseService.getCourseById(99L);

        assertThat(result)
                .as("If course is not found, it should return null")
                .isNull();
    }

    @Test
    void deleteCourse_shouldCallRepository() {
        courseService.deleteCourse(10L);

        verify(courseRepository, times(1)).deleteById(10L);
    }

    @Test
    void searchCourses_shouldDelegateToRepository() {
        when(courseRepository.searchCourses("math")).thenReturn(List.of(new Course()));

        List<Course> result = courseService.searchCourses("math");

        assertThat(result)
                .as("Search should return 1 result for keyword 'math'")
                .hasSize(1);
        verify(courseRepository).searchCourses("math");
    }

    @Test
    void updateCourse_existing_shouldUpdateFields() {
        Course existing = new Course();
        existing.setId(1L);
        existing.setTitle("Old Title");

        Course updated = new Course();
        updated.setTitle("New Title");
        updated.setDescription("desc");
        updated.setCredits(5);
        updated.setSemester(2);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Course result = courseService.updateCourse(1L, updated);

        assertThat(result.getTitle())
                .as("Title should be updated")
                .isEqualTo("New Title");
        assertThat(result.getDescription())
                .as("Description should be updated")
                .isEqualTo("desc");
        assertThat(result.getCredits())
                .as("Credits should be updated")
                .isEqualTo(5);
        assertThat(result.getSemester())
                .as("Semester should be updated")
                .isEqualTo(2);

        verify(courseRepository).save(existing);
    }

    @Test
    void updateCourse_notFound_shouldReturnNull() {
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        Course result = courseService.updateCourse(99L, new Course());

        assertThat(result)
                .as("If course is not found, update should return null")
                .isNull();
    }

    @Test
    void createCourse_shouldSetCurrentUserAndSave() {
        // Mock user in context
        String email = "test@prof.univ.ro";
        User mockUser = new User();
        mockUser.setId(42L);
        mockUser.setEmail(email);

        // Mock security context
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(email);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Mock UserRepository
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(mockUser));

        // Course to save
        Course inputCourse = new Course();
        inputCourse.setTitle("Algorithms");

        when(courseRepository.save(any(Course.class))).thenAnswer(inv -> inv.getArgument(0));

        Course saved = courseService.createCourse(inputCourse);

        assertThat(saved.getUser())
                .as("User should be set as the creator of the course")
                .isEqualTo(mockUser);
        verify(courseRepository).save(saved);
    }

    @Test
    void createCourse_withoutAuth_shouldThrow() {
        // Clear context (no authentication)
        SecurityContextHolder.clearContext();

        Course inputCourse = new Course();

        assertThatThrownBy(() -> courseService.createCourse(inputCourse))
                .as("Should throw RuntimeException when user is not authenticated")
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not authenticated");

        verify(courseRepository, never()).save(any());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
