package com.example.smarthub.models;

import com.example.smarthub.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role; // STUDENT, PROFESSOR, ADMIN

    @OneToOne(mappedBy = "userAccount")
    private Student student;

    @OneToOne(mappedBy = "userAccount")
    private Professor professor;
}