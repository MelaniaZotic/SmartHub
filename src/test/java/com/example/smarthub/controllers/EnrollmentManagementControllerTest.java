package com.example.smarthub.controllers;

import com.example.smarthub.CourseService;
import com.example.smarthub.EnrollmentService;
import com.example.smarthub.UserService;
import com.example.smarthub.enums.Role;
import com.example.smarthub.models.Course;
import com.example.smarthub.models.Enrollment;
import com.example.smarthub.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentManagementController.class)
class EnrollmentManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentService enrollmentService;

    @MockBean
    private UserService userService;

    @MockBean
    private CourseService courseService;

    private User professorUser;
    private Course ownCourse;
    private Course otherCourse;

    @BeforeEach
    void setUp() {
        professorUser = new User();
        professorUser.setId(1L);
        professorUser.setEmail("prof@univ.ro");
        professorUser.setUsername("Prof User");
        professorUser.setRoles(Set.of(Role.PROFESSOR));

        ownCourse = new Course();
        ownCourse.setId(10L);
        ownCourse.setTitle("Algorithms");

        otherCourse = new Course();
        otherCourse.setId(20L);
        otherCourse.setTitle("Physics");
    }

    @Test
    @WithMockUser(username = "prof@univ.ro", roles = {"PROFESSOR"})
    void getShowAddForm_asProfessor_returnsView() throws Exception {
        when(userService.getUserByEmail("prof@univ.ro")).thenReturn(professorUser);
        when(courseService.getCoursesByProfessorId(professorUser.getId())).thenReturn(List.of(ownCourse));
        when(userService.getAllStudents()).thenReturn(List.of());

        mockMvc.perform(get("/enrollments/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-enrollment"))
                .andExpect(model().attributeExists("enrollment"))
                .andExpect(model().attributeExists("students"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attributeExists("statuses"));
    }

    @Test
    @WithMockUser(username = "student@univ.ro", roles = {"STUDENT"})
    void getShowAddForm_asStudent_redirectsAccessDenied() throws Exception {
        User student = new User();
        student.setRoles(Set.of(Role.STUDENT));
        when(userService.getUserByEmail("student@univ.ro")).thenReturn(student);

        mockMvc.perform(get("/enrollments/add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    @Test
    @WithMockUser(username = "prof@univ.ro", roles = {"PROFESSOR"})
    void postAddEnrollment_validProfessorAndCourse_redirects() throws Exception {
        when(userService.getUserByEmail("prof@univ.ro")).thenReturn(professorUser);
        when(courseService.getCoursesByProfessorId(professorUser.getId())).thenReturn(List.of(ownCourse));

        mockMvc.perform(post("/enrollments/add")
                        .param("course.id", "10")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/enrollments"));

        verify(enrollmentService).createEnrollment(any());
    }

    @Test
    @WithMockUser(username = "prof@univ.ro", roles = {"PROFESSOR"})
    void postAddEnrollment_invalidCourse_redirectsAccessDenied() throws Exception {
        when(userService.getUserByEmail("prof@univ.ro")).thenReturn(professorUser);
        when(courseService.getCoursesByProfessorId(professorUser.getId())).thenReturn(List.of(ownCourse));

        mockMvc.perform(post("/enrollments/add")
                        .param("course.id", "20")  // invalid
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));

        verify(enrollmentService, never()).createEnrollment(any());
    }

    @Test
    @WithMockUser(username = "prof@univ.ro", roles = {"PROFESSOR"})
    void getListEnrollments_noSort() throws Exception {
        User student = new User();
        student.setUsername("Test Student");

        Course course = new Course();
        course.setTitle("Algorithms");

        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);

        when(enrollmentService.getAllEnrollments()).thenReturn(List.of(e));

        mockMvc.perform(get("/enrollments"))
                .andExpect(status().isOk())
                .andExpect(view().name("enrollments"))
                .andExpect(model().attributeExists("enrollments"));
    }
}
