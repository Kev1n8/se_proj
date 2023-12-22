package com.codeisright.attendance.dto;

import com.codeisright.attendance.data.Attendance;
import com.codeisright.attendance.data.AttendanceMeta;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class StudentMetaRecord {
    private final String metaId;

    private final int requirement; // 1, 2, 3 mapping by code, by location and by QR

    private final int status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime deadline;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime lastModified;

    private final Long latitude;

    private final Long longitude;

    private final String classId;

    public StudentMetaRecord(String metaId, int requirement, int status, LocalDateTime start, LocalDateTime deadline, LocalDateTime lastModified, Long latitude, Long longitude, String classId) {
        this.metaId = metaId;
        this.requirement = requirement;
        this.status = status;
        this.start = start;
        this.deadline = deadline;
        this.lastModified = lastModified;
        this.latitude = latitude;
        this.longitude = longitude;
        this.classId = classId;
    }

    public StudentMetaRecord(AttendanceMeta meta, Attendance record) {
        this.metaId = meta.getId();
        this.classId = meta.getAclass().getId();
        this.requirement = meta.getRequirement();
        this.status = record.getStatus();
        this.start = meta.getStart();
        this.deadline = meta.getDeadline();
        this.lastModified = record.getTime();
        this.latitude = meta.getLatitude();
        this.longitude = meta.getLongitude();
    }

    public String getMetaId() {
        return metaId;
    }

    public int getRequirement() {
        return requirement;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public Long getLatitude() {
        return latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public String getClassId() {
        return classId;
    }
}
