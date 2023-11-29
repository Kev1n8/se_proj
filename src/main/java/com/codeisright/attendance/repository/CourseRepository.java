package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, String> {

}
