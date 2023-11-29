package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Course;
import com.codeisright.attendance.data.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {
    public Course findByCourseName(String courseName);

    public Course findByCourseCode(String courseCode);

    public List<Course> findByTeacher_Name(String teacherName);

    public List<Course> findByLocation(String location);
}
