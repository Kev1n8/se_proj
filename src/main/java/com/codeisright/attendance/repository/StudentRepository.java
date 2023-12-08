package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Student;
import com.codeisright.attendance.view.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, String>{
    Student findStudentById(String id);
    StudentInfo findStudentInfoById(String id);
}