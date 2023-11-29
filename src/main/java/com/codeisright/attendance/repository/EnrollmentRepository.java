package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

}
