package com.example.smarthub.controllers;


import com.example.smarthub.enums.Role;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    private User studentUser;
    private User professorUser;

    @BeforeEach
    void setUp() {
        studentUser = new User();
        studentUser.setId(1L);
        studentUser.setEmail("student@s.univ.ro");
        studentUser.setRoles(Set.of(Role.STUDENT));

        professorUser = new User();
        professorUser.setId(2L);
        professorUser.setEmail("prof@prof.univ.ro");
        professorUser.setRoles(Set.of(Role.PROFESSOR));
    }

    @Test
    @WithMockUser(username = "student@s.univ.ro", roles = {"STUDENT"})
    void getStudents_asStudent_shouldSeeProfessors() throws Exception {
        // current logged in user
        when(userRepository.findByEmail("student@s.univ.ro")).thenReturn(Optional.of(studentUser));

        // all users in repo
        when(userRepository.findAll()).thenReturn(List.of(studentUser, professorUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/students"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("studentsPage"))
                .andExpect(model().attribute("isStudent", true))
                .andExpect(view().name("students"));
    }

    @Test
    @WithMockUser(username = "prof@prof.univ.ro", roles = {"PROFESSOR"})
    void getStudents_asProfessor_shouldSeeStudents() throws Exception {
        when(userRepository.findByEmail("prof@prof.univ.ro")).thenReturn(Optional.of(professorUser));
        when(userRepository.findAll()).thenReturn(List.of(studentUser, professorUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/students"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("studentsPage"))
                .andExpect(model().attribute("isProfessor", true))
                .andExpect(view().name("students"));
    }

    @Test
    @WithMockUser(username = "admin@univ.ro", roles = {"ADMIN"})
    void getStudents_asAdmin_shouldSeeStudents() throws Exception {
        User adminUser = new User();
        adminUser.setId(3L);
        adminUser.setEmail("admin@univ.ro");
        adminUser.setRoles(Set.of(Role.ADMIN));

        when(userRepository.findByEmail("admin@univ.ro")).thenReturn(Optional.of(adminUser));
        when(userRepository.findAll()).thenReturn(List.of(studentUser, professorUser));

        mockMvc.perform(MockMvcRequestBuilders.get("/students"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("studentsPage"))
                .andExpect(model().attribute("isAdmin", true))
                .andExpect(view().name("students"));
    }

}

