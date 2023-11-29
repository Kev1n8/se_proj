package com.codeisright.attendance.service;

import com.codeisright.attendance.data.Attendance;

import java.time.LocalDateTime;
import java.util.List;

public interface CheckinService {
    List<Attendance> getCheckin(String studentId, String classId);

    Attendance addCheckin(String studentId, String classId, int status, LocalDateTime time);

    Attendance forwardCheckin(String studentId, String classId, LocalDateTime time);

    boolean deleteStudentClassIdCheckin(String studentId, String classId);

    List<Attendance> getCheckinByClass(String classId, LocalDateTime start, LocalDateTime end);

    List<Attendance> getCheckinByStudent(String studentId, LocalDateTime start, LocalDateTime end);

    boolean canCheckin(String studentId, String classId , LocalDateTime time);
}
