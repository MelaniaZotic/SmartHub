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
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String title;
    private String description;
    private int credits;
    private int semester;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;

    @OneToMany(mappedBy = "course")
    private List<Grade> grades;

    @OneToMany(mappedBy = "course")
    private List<Schedule> schedules;

    @OneToOne(mappedBy = "course")
    private Exam exam;

    @OneToMany(mappedBy = "course")
    private List<Feedback> feedbacks;
}
