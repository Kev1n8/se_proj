package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    List<Attendance> findByAclass_Id(String classId);

    List<Attendance> findByAclass_IdAndMeta_Id(String classId, String metaId);

    Attendance findByStudent_IdAndMeta_Id(String studentId, String metaId);
}
