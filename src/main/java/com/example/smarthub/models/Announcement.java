package com.example.smarthub.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private LocalDate publishDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    private String targetAudience; // STUDENTS / PROFESSORS / ALL
}
