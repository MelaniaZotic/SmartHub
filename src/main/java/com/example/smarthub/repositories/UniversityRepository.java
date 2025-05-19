package com.example.smarthub.repositories;

import com.example.smarthub.models.University;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniversityRepository extends JpaRepository<University,Long> {
}
