package com.example.smarthub.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {


    @GetMapping("/main")
    public String homePage()
    {
        return "index";
    }
}
