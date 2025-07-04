package com.example.smarthub.services;

import com.example.smarthub.models.Grade;
import com.example.smarthub.repositories.GradeRepository;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;

    private static final Logger logger = LoggerFactory.getLogger(GradeServiceImpl.class);

    public GradeServiceImpl(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    @Override
    public List<Grade> getAllGrades() {
        logger.info("Fetching all grades");
        return gradeRepository.findAll();
    }

    @Override
    public Grade getGradeById(Long id) {
        logger.info("Fetching grade by ID: {}", id);
        return gradeRepository.findById(id).orElse(null);
    }

    @Override
    public Grade createGrade(Grade grade) {
        logger.info("Creating new grade: {}", grade);
        return gradeRepository.save(grade);
    }

    @Override
    public Grade updateGrade(Long id, Grade grade) {
        logger.info("Updating grade ID: {}", id);
        return gradeRepository.findById(id)
                .map(existing -> {
                    existing.setValue(grade.getValue());
                    existing.setDate(grade.getDate());
                    existing.setType(grade.getType());
                    existing.setCourse(grade.getCourse());
                    logger.info("Grade updated: {}", existing);
                    return gradeRepository.save(existing);
                }).orElse(null);
    }

    @Override
    public void deleteGrade(Long id) {
        logger.info("Deleting grade ID: {}", id);
        gradeRepository.deleteById(id);
    }

}
