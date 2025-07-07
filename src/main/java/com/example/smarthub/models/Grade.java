package com.example.smarthub.models;
import com.example.smarthub.enums.GradeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double value;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private GradeType type; // MIDTERM, FINAL

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public GradeType getType() {
        return type;
    }

    public void setType(GradeType type) {
        this.type = type;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}

