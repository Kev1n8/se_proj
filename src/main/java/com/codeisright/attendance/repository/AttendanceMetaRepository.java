package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.AttendanceMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AttendanceMetaRepository extends JpaRepository<AttendanceMeta, String> {
    List<AttendanceMetaRepository> findAttendanceMetasByDeadlineBefore(LocalDateTime ddl);

    AttendanceMeta findFirstByIdAndIdOrderByStartDesc(String studentId, String classId);

    List<AttendanceMeta> findByAclass_Id(String classId);
}
