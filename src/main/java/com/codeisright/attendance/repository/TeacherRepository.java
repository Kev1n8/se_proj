package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.view.TeacherInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, String> {
    List<TeacherInfo> findByDepartment(String department);

    TeacherInfo findTeacherInfoById(String id);

    Teacher findByName(String teacherName);
}