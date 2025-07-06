package com.example.smarthub.controllers;

import com.example.smarthub.enums.Role;
import com.example.smarthub.models.Announcement;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.AnnouncementRepository;
import com.example.smarthub.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AnnouncementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnnouncementRepository announcementRepository;

    @MockBean
    private UserRepository userRepository;

    private User professorUser;
    private User studentUser;

    @BeforeEach
    void setup() {
        professorUser = new User();
        professorUser.setEmail("prof@univ.ro");
        professorUser.setRoles(Set.of(Role.PROFESSOR));

        studentUser = new User();
        studentUser.setEmail("stud@univ.ro");
        studentUser.setRoles(Set.of(Role.STUDENT));

        // Default mock: announcementRepository returns 1 announcement with author
        Announcement announcement = new Announcement();
        announcement.setId(1L);
        announcement.setTitle("Test Announcement");
        announcement.setContent("Test Content");
        announcement.setPublishDate(LocalDate.now());
        announcement.setAuthor(professorUser);

        given(announcementRepository.findAll()).willReturn(List.of(announcement));
    }

    //
    // 1. GET /announcements - ANONYMOUS ACCESS
    //
    @Test
    void getAnnouncements_asAnonymous_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/announcements"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }


    //
    // 2. GET /announcements - LOGGED IN STUDENT
    //
    @Test
    void getAnnouncements_asStudent_returnsOk() throws Exception {
        given(userRepository.findByEmail("stud@univ.ro")).willReturn(Optional.of(studentUser));

        mockMvc.perform(get("/announcements")
                        .with(user("stud@univ.ro").roles("STUDENT")))
                .andExpect(status().isOk())
                .andExpect(view().name("announcements"));
    }

    //
    // 3. GET /announcements/new - AS PROFESSOR
    //
    @Test
    void getCreateForm_asProfessor_returnsForm() throws Exception {
        given(userRepository.findByEmail("prof@univ.ro")).willReturn(Optional.of(professorUser));

        mockMvc.perform(get("/announcements/new")
                        .with(user("prof@univ.ro").roles("PROFESSOR")))
                .andExpect(status().isOk())
                .andExpect(view().name("create-announcement"));
    }

    //
    // 4. GET /announcements/new - AS STUDENT (DENIED)
    //
    @Test
    void getCreateForm_asStudent_redirectsAccessDenied() throws Exception {
        given(userRepository.findByEmail("stud@univ.ro")).willReturn(Optional.of(studentUser));

        mockMvc.perform(get("/announcements/new")
                        .with(user("stud@univ.ro").roles("STUDENT")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

    //
    // 5. POST /announcements/save - AS PROFESSOR
    //
    @Test
    void postSave_asProfessor_savesAndRedirects() throws Exception {
        given(userRepository.findByEmail("prof@univ.ro")).willReturn(Optional.of(professorUser));

        mockMvc.perform(post("/announcements/save")
                        .with(csrf())
                        .with(user("prof@univ.ro").roles("PROFESSOR"))
                        .param("title", "New Title")
                        .param("content", "New Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/announcements"));
    }

    //
    // 6. POST /announcements/save - AS STUDENT (DENIED)
    //
    @Test
    void postSave_asStudent_redirectsAccessDenied() throws Exception {
        given(userRepository.findByEmail("stud@univ.ro")).willReturn(Optional.of(studentUser));

        mockMvc.perform(post("/announcements/save")
                        .with(csrf())
                        .with(user("stud@univ.ro").roles("STUDENT"))
                        .param("title", "Bad")
                        .param("content", "Bad"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/access-denied"));
    }

}
