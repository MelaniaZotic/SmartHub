package com.example.smarthub.models;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;
    private int score; // 1 to 5
//
//    @ManyToOne
//    @JoinColumn(name = "student_id")
//    private Student student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
