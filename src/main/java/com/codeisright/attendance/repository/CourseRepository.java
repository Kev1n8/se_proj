package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Course;
import com.codeisright.attendance.data.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String> {
    Course findByCourseName(String courseName);

    Course findByCourseCode(String courseCode);

    Course findByName(String courseName);
}
