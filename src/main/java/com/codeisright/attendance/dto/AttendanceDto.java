package com.codeisright.attendance.dto;

import com.codeisright.attendance.data.Attendance;

import java.time.LocalDateTime;
import java.util.Map;

public class AttendanceDto {
    private final String studentId;

    private final String  classId;

    private final String metaId;

    private final int status;

    private final LocalDateTime time;

    private final Long latitude;

    private final Long longitude;

    public AttendanceDto(Attendance attendance){
        if (attendance != null){
            this.studentId = attendance.getStudent().getUsername();
            this.classId = attendance.getAclass().getId();
            this.metaId = attendance.getMeta().getId();
            this.status = attendance.getStatus();
            this.time = attendance.getTime();
            this.latitude = attendance.getLatitude();
            this.longitude = attendance.getLongitude();
        }
        else {
            this.studentId = "";
            this.classId = "";
            this.metaId = "";
            this.status = 0;
            this.time = LocalDateTime.now();
            this.latitude = 0L;
            this.longitude = 0L;
        }
    }

    public AttendanceDto(String studentId, String classId, String metaId, int status, LocalDateTime time,
                         Long latitude, Long longitude) {
        this.studentId = studentId;
        this.classId = classId;
        this.metaId = metaId;
        this.status = status;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getClassId() {
        return classId;
    }

    public String getMetaId() {
        return metaId;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public Long getLatitude() {
        return latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "AttendanceDto{" +
                "studentId='" + studentId + '\'' +
                ", classId='" + classId + '\'' +
                ", metaId='" + metaId + '\'' +
                ", status=" + status +
                ", time=" + time +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public Map<String, String> toMap() {
        return Map.of(
                "studentId", studentId,
                "classId", classId,
                "metaId", metaId,
                "status", String.valueOf(status),
                "time", time.toString(),
                "latitude", String.valueOf(latitude),
                "longitude", String.valueOf(longitude)
        );
    }
}
