package com.example.smarthub.services;

import com.example.smarthub.models.Course;
import com.example.smarthub.models.Enrollment;
import com.example.smarthub.repositories.EnrollmentRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEnrollments_shouldReturnList() {
        List<Enrollment> mockList = List.of(new Enrollment(), new Enrollment());
        when(enrollmentRepository.findAll()).thenReturn(mockList);

        List<Enrollment> result = enrollmentService.getAllEnrollments();

        assertThat(result)
                .as("Should return all enrollments from repository")
                .hasSize(2);
        verify(enrollmentRepository).findAll();
    }

    @Test
    void getEnrollmentById_found_shouldReturnEnrollment() {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(1L);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        Enrollment result = enrollmentService.getEnrollmentById(1L);

        assertThat(result)
                .as("Enrollment should be returned when found")
                .isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getEnrollmentById_notFound_shouldReturnNull() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        Enrollment result = enrollmentService.getEnrollmentById(99L);

        assertThat(result)
                .as("Should return null if not found")
                .isNull();
    }

    @Test
    void createEnrollment_shouldSaveAndReturnEnrollment() {
        Enrollment toSave = new Enrollment();
        //toSave.setStatus(EnrollmentStatus.NEW);
        toSave.setEnrollmentDate(LocalDate.now());

        Enrollment saved = new Enrollment();
        saved.setId(10L);
        //saved.setStatus(EnrollmentStatus.NEW);
        saved.setEnrollmentDate(LocalDate.now());

        when(enrollmentRepository.save(toSave)).thenReturn(saved);

        Enrollment result = enrollmentService.createEnrollment(toSave);

        assertThat(result)
                .as("Created enrollment should not be null")
                .isNotNull();
        assertThat(result.getId())
                .as("Created enrollment should have an ID")
                .isEqualTo(10L);
//        assertThat(result.getStatus())
//                .as("Created enrollment should have correct status")
//                .isEqualTo(EnrollmentStatus.NEW);

        verify(enrollmentRepository).save(toSave);
    }

    @Test
    void updateEnrollment_existing_shouldUpdateFields() {
        Enrollment existing = new Enrollment();
        existing.setId(1L);
        //existing.setStatus(EnrollmentStatus.PENDING);
        existing.setEnrollmentDate(LocalDate.of(2024, 1, 1));

        Course existingCourse = new Course();
        existingCourse.setId(2L);
        existing.setCourse(existingCourse);

        Enrollment updates = new Enrollment();
        //updates.setStatus(EnrollmentStatus.APPROVED);
        updates.setEnrollmentDate(LocalDate.of(2024, 2, 2));

        Course newCourse = new Course();
        newCourse.setId(5L);
        newCourse.setTitle("Algorithms");
        updates.setCourse(newCourse);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(inv -> inv.getArgument(0));

        Enrollment result = enrollmentService.updateEnrollment(1L, updates);

        assertThat(result)
                .as("Updated enrollment should not be null")
                .isNotNull();
//        assertThat(result.getStatus())
//                .as("Status should be updated")
//                .isEqualTo(EnrollmentStatus.APPROVED);
        assertThat(result.getEnrollmentDate())
                .as("Enrollment date should be updated")
                .isEqualTo(LocalDate.of(2024, 2, 2));
        assertThat(result.getCourse())
                .as("Course should be updated")
                .isEqualTo(newCourse);

        verify(enrollmentRepository).save(existing);
    }

    @Test
    void updateEnrollment_notFound_shouldReturnNull() {
        when(enrollmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        Enrollment updates = new Enrollment();
        //updates.setStatus(EnrollmentStatus.APPROVED);

        Enrollment result = enrollmentService.updateEnrollment(42L, updates);

        assertThat(result)
                .as("Should return null if enrollment to update is not found")
                .isNull();
    }

    @Test
    void deleteEnrollment_shouldCallRepository() {
        enrollmentService.deleteEnrollment(77L);

        verify(enrollmentRepository, times(1)).deleteById(77L);
    }
}
