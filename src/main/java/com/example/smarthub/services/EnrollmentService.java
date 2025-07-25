package com.example.smarthub.services;

import com.example.smarthub.models.Enrollment;

import java.util.List;

public interface EnrollmentService {
    List<Enrollment> getAllEnrollments();
    Enrollment getEnrollmentById(Long id);
    Enrollment createEnrollment(Enrollment enrollment);
    Enrollment updateEnrollment(Long id, Enrollment enrollment);
    void deleteEnrollment(Long id);
    List<Enrollment> getEnrollmentsByCourseId(Long courseId);
}
