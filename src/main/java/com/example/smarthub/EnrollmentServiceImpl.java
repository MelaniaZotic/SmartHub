package com.example.smarthub;

import com.example.smarthub.models.Enrollment;
import com.example.smarthub.repositories.EnrollmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
    public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id).orElse(null);
    }

    @Override
    public Enrollment createEnrollment(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment updateEnrollment(Long id, Enrollment enrollment) {
        return enrollmentRepository.findById(id)
                .map(existing -> {
                    existing.setStatus(enrollment.getStatus());
                    existing.setEnrollmentDate(enrollment.getEnrollmentDate());
//                    existing.setStudent(enrollment.getStudent());
                    existing.setCourse(enrollment.getCourse());
                    return enrollmentRepository.save(existing);
                }).orElse(null);
    }

    @Override
    public void deleteEnrollment(Long id) {
        enrollmentRepository.deleteById(id);
    }
}

