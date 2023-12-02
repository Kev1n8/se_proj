package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Enrollment;
import com.codeisright.attendance.data.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {
    List<Enrollment> findStudentByAclass_Id(String classId);

    List<Enrollment> findAclassByStudent_Id(String studentId);

    Enrollment findByAclass_IdAndStudent_Id(String classId, String studentId);
}
