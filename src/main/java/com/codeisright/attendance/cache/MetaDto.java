package com.codeisright.attendance.cache;

import java.time.LocalDateTime;

public class MetaDto {
    private int requirement; // 1, 2, 3 mapping by code, by location and by QR

    private LocalDateTime start;

    private LocalDateTime deadline;

    private Long latitude;

    private Long longitude;

    private String classId;

    public int getRequirement() {
        return requirement;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getDeadline() {
        return deadline;
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
