package com.codeisright.attendance.cache;

import java.time.LocalDateTime;

public class AttendanceDto {
    private String studentId;

    private String  classId;

    private String metaId;

    private int status;

    private LocalDateTime time;

    private Long latitude;

    private Long longitude;

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
}
