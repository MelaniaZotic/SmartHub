package com.example.smarthub.controllers;

import com.example.smarthub.services.CourseService;
import com.example.smarthub.models.Course;
import com.example.smarthub.models.User;
import com.example.smarthub.enums.Role;
import com.example.smarthub.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseManagementController.class)
class CourseManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    @MockBean
    private UserRepository userRepository;

    private User studentUser;
    private User professorUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        studentUser = new User();
        studentUser.setId(1L);
        studentUser.setEmail("student@univ.ro");
        studentUser.setUsername("Student User");
        studentUser.setRoles(Set.of(Role.STUDENT));

        professorUser = new User();
        professorUser.setId(2L);
        professorUser.setEmail("prof@univ.ro");
        professorUser.setUsername("Professor User");
        professorUser.setRoles(Set.of(Role.PROFESSOR));

        adminUser = new User();
        adminUser.setId(3L);
        adminUser.setEmail("admin@univ.ro");
        adminUser.setUsername("Admin User");
        adminUser.setRoles(Set.of(Role.ADMIN));
    }

    @Test
    @WithMockUser(username = "student@univ.ro", roles = {"STUDENT"})
    void getCourses_asStudent() throws Exception {
        when(userRepository.findByEmail("student@univ.ro")).thenReturn(Optional.of(studentUser));
        when(courseService.getCoursesByStudentId(1L)).thenReturn(List.of(new Course()));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("isStudent", true))
                .andExpect(model().attribute("isProfessor", false));
    }

    @Test
    @WithMockUser(username = "prof@univ.ro", roles = {"PROFESSOR"})
    void getCourses_asProfessor() throws Exception {
        when(userRepository.findByEmail("prof@univ.ro")).thenReturn(Optional.of(professorUser));
        when(courseService.getCoursesByProfessorId(2L)).thenReturn(List.of(new Course()));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("isProfessor", true))
                .andExpect(model().attribute("isStudent", false));
    }

    @Test
    @WithMockUser(username = "admin@univ.ro", roles = {"ADMIN"})
    void getCourses_asAdmin_withKeyword() throws Exception {
        when(userRepository.findByEmail("admin@univ.ro")).thenReturn(Optional.of(adminUser));
        when(courseService.searchCourses("algo")).thenReturn(List.of(new Course()));

        mockMvc.perform(get("/courses").param("keyword", "algo"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses"))
                .andExpect(model().attributeExists("courses"))
                .andExpect(model().attribute("keyword", "algo"));
    }

    @Test
    @WithMockUser(username = "admin@univ.ro", roles = {"ADMIN"})
    void getCourses_asAdmin_noKeyword() throws Exception {
        when(userRepository.findByEmail("admin@univ.ro")).thenReturn(Optional.of(adminUser));

        // create a course with user set
        Course course = new Course();
        User courseUser = new User();
        courseUser.setId(3L);
        course.setUser(courseUser);

        when(courseService.getAllCourses()).thenReturn(List.of(course));

        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(view().name("courses"))
                .andExpect(model().attributeExists("courses"));
    }


    @Test
    @WithMockUser
    void getShowAddForm() throws Exception {
        mockMvc.perform(get("/courses/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("add-course"))
                .andExpect(model().attributeExists("course"));
    }

    @Test
    @WithMockUser(username = "prof@univ.ro", roles = {"PROFESSOR"})
    void postAddCourse() throws Exception {
        when(userRepository.findByEmail("prof@univ.ro")).thenReturn(Optional.of(professorUser));

        mockMvc.perform(post("/courses/add")
                        .param("title", "Algorithms")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        verify(courseService).createCourse(any());
    }

    @Test
    @WithMockUser
    void getShowEditForm_found() throws Exception {
        Course course = new Course();
        course.setId(10L);

        when(courseService.getCourseById(10L)).thenReturn(course);

        mockMvc.perform(get("/courses/edit/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-course"))
                .andExpect(model().attributeExists("course"));
    }

    @Test
    @WithMockUser
    void getShowEditForm_notFound() throws Exception {
        when(courseService.getCourseById(10L)).thenReturn(null);

        mockMvc.perform(get("/courses/edit/10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));
    }

    @Test
    @WithMockUser
    void postEditCourse() throws Exception {
        mockMvc.perform(post("/courses/edit/10")
                        .param("title", "Algorithms")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        verify(courseService).updateCourse(eq(10L), any());
    }

    @Test
    @WithMockUser
    void getDeleteCourse() throws Exception {
        mockMvc.perform(get("/courses/delete/10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/courses"));

        verify(courseService).deleteCourse(10L);
    }
}
