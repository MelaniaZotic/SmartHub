package com.example.smarthub.controllers;

import com.example.smarthub.models.Announcement;
import com.example.smarthub.models.User;
import com.example.smarthub.repositories.AnnouncementRepository;
import com.example.smarthub.repositories.UserRepository;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;
    private final UserRepository userRepository;

    public AnnouncementController(AnnouncementRepository announcementRepository, UserRepository userRepository) {
        this.announcementRepository = announcementRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String viewAnnouncements(Model model,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        List<Announcement> announcements = announcementRepository.findAll();
        User loggedUser = null;
        if (userDetails != null) {
            loggedUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }

        model.addAttribute("announcements", announcements);
        model.addAttribute("loggedUser", loggedUser);

        return "announcements";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {

        boolean isProfessor = false;
        if (userDetails != null) {
            User loggedUser = userRepository.findByEmail(userDetails.getUsername()).orElse(null);
            if (loggedUser != null) {
                isProfessor = loggedUser.getRoles().stream()
                        .anyMatch(role -> role.name().equals("PROFESSOR"));
            }
        }

        if (!isProfessor) {
            return "redirect:/access-denied";
        }

        model.addAttribute("announcement", new Announcement());
        return "create-announcement";
    }

    @PostMapping("/save")
    public String saveAnnouncement(@ModelAttribute Announcement announcement,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        User professor = userRepository.findByEmail(userDetails.getUsername()).orElse(null);

        if (professor != null) {
            boolean isProfessor = professor.getRoles().stream()
                    .anyMatch(role -> role.name().equals("PROFESSOR"));
            if (!isProfessor) {
                return "redirect:/access-denied";
            }

            announcement.setAuthor(professor);
            announcement.setPublishDate(LocalDate.now());
            announcementRepository.save(announcement);
        }

        return "redirect:/announcements";
    }
}
