package com.codeisright.attendance.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AttendanceMeta {
    @Id
    private String id;

    private int requirement; // 1, 2, 3 mapping by code, by location and by QR

    private LocalDateTime start;

    private LocalDateTime deadline;

    private Long latitude;

    private Long longitude;

    @ManyToOne
    @JoinColumn(name = "classId")
    private Aclass aclass;

    protected AttendanceMeta() {
    }

    public AttendanceMeta(int requirement, LocalDateTime start, LocalDateTime deadline, Aclass aclass, Long latitude, Long longitude) {
        this.requirement = requirement;
        this.start = start;
        this.deadline = deadline;
        this.aclass = aclass;
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getRequirement() {
        return requirement;
    }

    public void setRequirement(int requirement) {
        this.requirement = requirement;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime startTime) {
        this.start = startTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public Aclass getAclass() {
        return aclass;
    }

    public void setAclass(Aclass aClass) {
        this.aclass = aClass;
    }

    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "AttendanceMeta{" +
                "id='" + id + '\'' +
                ", requirement=" + requirement +
                ", start=" + start +
                ", deadline=" + deadline +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", aclass=" + aclass +
                '}';
    }
}
