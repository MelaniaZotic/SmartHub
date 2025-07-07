package com.example.smarthub.controllers;

import com.example.smarthub.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;



@Controller
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class
AdminUserController {

    private final UserService userService;
    public AdminUserController(UserService us){ this.userService = us;}

    @GetMapping
    public String pending(Model model){
        model.addAttribute("pendingUsers", userService.getPendingUsers());
        return "admin-users";
    }

    @PostMapping("/approve/{id}")
    public String approve(@PathVariable Long id){
        userService.approveUser(id);
        return "redirect:/admin/users?approved";
    }
}

