package com.example.smarthub.services;

import com.example.smarthub.models.Student;
import com.example.smarthub.repositories.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        return studentRepository.findById(id)
                .map(existing -> {
                    existing.setName(student.getName());
                    existing.setEmail(student.getEmail());
                    existing.setStudentGroup(student.getStudentGroup());
                    existing.setYear(student.getYear());
                    existing.setSpecialization(student.getSpecialization());
                    return studentRepository.save(existing);
                }).orElse(null);
    }

    @Override
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

}