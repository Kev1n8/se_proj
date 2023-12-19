package com.codeisright.attendance.data;

import com.codeisright.attendance.dto.MetaDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class AttendanceMeta {
    @Id
    private String id;

    private int requirement; // 1, 2, 3 mapping by code, by location and by QR

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deadline;

    private Long latitude;

    private Long longitude;

    private boolean notified = false;

    @ManyToOne
    @JoinColumn(name = "classId")
    private Aclass aclass;

    protected AttendanceMeta() {
    }

    public AttendanceMeta(String id, MetaDto meta, Aclass aclass) {
        this.id = id;
        this.requirement = meta.getRequirement();
        this.start = meta.getStart();
        this.deadline = meta.getDeadline();
        this.latitude = meta.getLatitude();
        this.longitude = meta.getLongitude();
        this.aclass = aclass;
        this.notified = false;
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

    public boolean hasNotified() {
        return notified;
    }

    public void setNotified(boolean notified) {
        this.notified = notified;
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
