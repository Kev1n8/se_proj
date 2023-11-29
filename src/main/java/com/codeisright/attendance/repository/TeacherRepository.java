package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, String> {
    public List<Teacher> findByDepartment(String department);
}
