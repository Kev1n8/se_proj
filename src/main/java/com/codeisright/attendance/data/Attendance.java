package com.codeisright.attendance.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;

    @ManyToOne
    @JoinColumn(name="studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name="classId")
    private Aclass aclass;

    @ManyToOne
    @JoinColumn(name="metaId")
    private AttendanceMeta meta;
    private int status;

    private LocalDateTime time;

    protected Attendance() {}

    public Attendance(Student student, Aclass aClass, int status, LocalDateTime time) {
        this.student = student;
        this.aclass = aClass;
        this.status = status;
        assert this.status <= 3;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Aclass getAClass() {
        return aclass;
    }

    public void setAClass(Aclass aClass) {
        this.aclass = aClass;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public void forward() {
        this.status++;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id='" + id + '\'' +
                ", student=" + student +
                ", class=" + aclass +
                ", status='" + status + '\'' +
                ", time=" + time +
                '}';
    }
}
