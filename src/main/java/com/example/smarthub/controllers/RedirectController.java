package com.example.smarthub.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    @GetMapping("/")

    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        logger.info("Displaying login page");
        return "login";
    }
    @GetMapping("/register")

    public String registerPage() {
        logger.info("Displaying register page");
        return "register";
    }
}
