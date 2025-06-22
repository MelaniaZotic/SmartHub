package com.example.smarthub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.net.URI;

@SpringBootApplication
public class SmartHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartHubApplication.class, args);

        // Deschide browserul după start
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI("http://localhost:8086"));
            }
        } catch (Exception e) {
            System.out.println("Nu s-a putut deschide browserul: " + e.getMessage());
        }
    }
}
