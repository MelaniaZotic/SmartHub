package com.example.smarthub;

import com.example.smarthub.models.Course;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course getCourseById(Long id);
    Course createCourse(Course course);
    Course updateCourse(Long id, Course course);
    void deleteCourse(Long id);
    List<Course> searchCourses(String keyword);

    List<Course> getCoursesByProfessorId(Long professorId);
    List<Course> getCoursesByStudentId(Long studentId);


}