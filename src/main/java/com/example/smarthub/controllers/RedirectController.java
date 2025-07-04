package com.example.smarthub.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class RedirectController {
    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    @GetMapping("/")
    public String redirectToLogin() {
        logger.info("Redirecting to /login");
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
