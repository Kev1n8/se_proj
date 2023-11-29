package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, String> {

}
