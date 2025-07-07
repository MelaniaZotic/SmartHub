package com.example.smarthub.controllers;

import com.example.smarthub.enums.Role;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    private User studentUser;
    private User professorUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        studentUser = new User();
        studentUser.setId(1L);
        studentUser.setEmail("student@s.univ.ro");
        studentUser.setUsername("Student User");
        studentUser.setRoles(Set.of(Role.STUDENT));

        professorUser = new User();
        professorUser.setId(2L);
        professorUser.setEmail("prof@prof.univ.ro");
        professorUser.setUsername("Professor User");
        professorUser.setRoles(Set.of(Role.PROFESSOR));

        adminUser = new User();
        adminUser.setId(3L);
        adminUser.setEmail("admin@univ.ro");
        adminUser.setUsername("Admin User");
        adminUser.setRoles(Set.of(Role.ADMIN));
    }

    @Test
    @WithMockUser(username = "student@s.univ.ro", roles = {"STUDENT"})
    void dashboard_asStudent() throws Exception {
        when(userRepository.findByEmail("student@s.univ.ro")).thenReturn(Optional.of(studentUser));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("username", "Student User"))
                .andExpect(model().attribute("isStudent", true))
                .andExpect(model().attribute("isProfessor", false))
                .andExpect(model().attribute("isAdmin", false));
    }

    @Test
    @WithMockUser(username = "prof@prof.univ.ro", roles = {"PROFESSOR"})
    void dashboard_asProfessor() throws Exception {
        when(userRepository.findByEmail("prof@prof.univ.ro")).thenReturn(Optional.of(professorUser));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("username", "Professor User"))
                .andExpect(model().attribute("isProfessor", true))
                .andExpect(model().attribute("isStudent", false))
                .andExpect(model().attribute("isAdmin", false));
    }

    @Test
    @WithMockUser(username = "admin@univ.ro", roles = {"ADMIN"})
    void dashboard_asAdmin() throws Exception {
        when(userRepository.findByEmail("admin@univ.ro")).thenReturn(Optional.of(adminUser));

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("username", "Admin User"))
                .andExpect(model().attribute("isAdmin", true))
                .andExpect(model().attribute("isProfessor", false))
                .andExpect(model().attribute("isStudent", false));
    }

    @Test
    @WithMockUser(username = "ghost@univ.ro")
    void dashboard_userNotFound() throws Exception {
        when(userRepository.findByEmail("ghost@univ.ro")).thenReturn(Optional.empty());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("username", "Utilizator"))
                .andExpect(model().attribute("isAdmin", false))
                .andExpect(model().attribute("isProfessor", false))
                .andExpect(model().attribute("isStudent", false));
    }

    @Test
    void dashboard_noAuthentication() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isUnauthorized());
    }
}
