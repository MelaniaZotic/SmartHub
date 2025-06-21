package com.example.smarthub.services;

import com.example.smarthub.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();
    Student getStudentById(Long id);
    Student createStudent(Student student);
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);
    Page<Student> searchStudents(String search, Pageable pageable);
    Page<Student> findAllPaged(Pageable pageable);

}
