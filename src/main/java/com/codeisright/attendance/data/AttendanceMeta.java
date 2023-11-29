package com.codeisright.attendance.data;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;

@Entity
public class AttendanceMeta {
    @Id
    @GeneratedValue(generator = "random-id")
    @GenericGenerator(name = "random-id", strategy = "com.codeisright.attendance.util.RandomIdGenerator")
    private String id;

    private int requirement; // 1, 2, 3 mapping by code, by location and by QR

    private LocalDateTime startTime;

    private LocalDateTime deadline;

    @ManyToOne
    @JoinColumn(name = "classId")
    private AClass aClass;

    protected AttendanceMeta() {
    }

    public AttendanceMeta(int requirement, LocalDateTime startTime, LocalDateTime deadline, AClass aClass) {
        this.requirement = requirement;
        this.startTime = startTime;
        this.deadline = deadline;
        this.aClass = aClass;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public AClass getaClass() {
        return aClass;
    }

    public void setaClass(AClass aClass) {
        this.aClass = aClass;
    }

    @Override
    public String toString() {
        return "AttendanceMeta{" +
                "id='" + id + '\'' +
                ", requirement=" + requirement +
                ", startTime=" + startTime +
                ", deadline=" + deadline +
                ", aClass=" + aClass +
                '}';
    }
}
