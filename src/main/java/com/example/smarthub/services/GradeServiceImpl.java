package com.example.smarthub.services;

import com.example.smarthub.models.Grade;
import com.example.smarthub.repositories.GradeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;

    public GradeServiceImpl(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }

    @Override
    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id).orElse(null);
    }

    @Override
    public Grade createGrade(Grade grade) {
        return gradeRepository.save(grade);
    }

    @Override
    public Grade updateGrade(Long id, Grade grade) {
        return gradeRepository.findById(id)
                .map(existing -> {
                    existing.setValue(grade.getValue());
                    existing.setDate(grade.getDate());
                    existing.setType(grade.getType());
//                    existing.setStudent(grade.getStudent());
                    existing.setCourse(grade.getCourse());
                    return gradeRepository.save(existing);
                }).orElse(null);
    }

    @Override
    public void deleteGrade(Long id) {
        gradeRepository.deleteById(id);
    }

}
