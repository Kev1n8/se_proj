package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Student;
import com.codeisright.attendance.view.StudentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, String>{
    StudentInfo findStudentInfoById(String id);
}