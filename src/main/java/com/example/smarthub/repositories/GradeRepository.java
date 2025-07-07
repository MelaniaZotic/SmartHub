package com.example.smarthub.repositories;

import com.example.smarthub.models.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade,Long> {
    Optional<Grade> findByStudentIdAndCourseId(Long studentId, Long courseId);
    List<Grade> findByStudentId(Long studentId);

}
