package com.codeisright.attendance.data;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
public class AttendanceMeta {
    @Id
    @GeneratedValue(generator = "random-id")
    @GenericGenerator(name = "random-id", strategy = "com.codeisright.attendance.utils.RandomIdGenerator")
    private String id;

    private int requirement; // 1, 2, 3 mapping by code, by location and by QR

    private LocalDateTime start;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "classId")
    private Aclass aclass;

    protected AttendanceMeta() {
    }

    public AttendanceMeta(int requirement, LocalDateTime start, LocalDateTime deadline, Aclass aclass) {
        this.requirement = requirement;
        this.start = start;
        this.deadline = deadline;
        this.aclass = aclass;
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

    public Aclass getaAclass() {
        return aclass;
    }

    public void setaAclass(Aclass aClass) {
        this.aclass = aClass;
    }

    @Override
    public String toString() {
        return "AttendanceMeta{" +
                "id='" + id + '\'' +
                ", requirement=" + requirement +
                ", startTime=" + start +
                ", deadline=" + deadline +
                ", aClass=" + aclass +
                '}';
    }
}
