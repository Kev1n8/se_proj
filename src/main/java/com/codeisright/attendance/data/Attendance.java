package com.codeisright.attendance.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

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

    private Long latitude;

    private Long longitude;

    protected Attendance() {}

    public Attendance(Student student, Aclass aClass, int status, LocalDateTime time, Long latitude, Long longitude) {
        this.student = student;
        this.aclass = aClass;
        this.status = status;
        assert this.status <= 3;
        this.time = time;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Aclass getAclass() {
        return aclass;
    }

    public void setAclass(Aclass aclass) {
        this.aclass = aclass;
    }

    public AttendanceMeta getMeta() {
        return meta;
    }

    public void setMeta(AttendanceMeta meta) {
        this.meta = meta;
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
