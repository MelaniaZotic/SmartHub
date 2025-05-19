package com.example.smarthub.repositories;

import com.example.smarthub.models.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository  extends JpaRepository<Schedule,Long> {
}
