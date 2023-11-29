package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findByStudentIdAndClassId(String studentId, String classId);

    List<Attendance> findByClassIdAndTimeBetween(String classId, LocalDateTime start, LocalDateTime end);

    List<Attendance> findByStudentIdAndTimeBetween(String studentId, LocalDateTime start, LocalDateTime end);
}
