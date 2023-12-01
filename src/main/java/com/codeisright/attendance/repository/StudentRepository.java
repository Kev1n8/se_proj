package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String>{
    List<Student> findByTheclass(String theclass);
}