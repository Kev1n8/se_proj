package com.codeisright.attendance.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Attendance {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private String id;

    @ManyToOne
    @JoinColumn(name="studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name="courseId")
    private Course course;

    private String status;

    private LocalDateTime time;

    protected Attendance() {}

    public Attendance(String id, Student student, Course course, String status, LocalDateTime time) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.status = status;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "id='" + id + '\'' +
                ", student=" + student +
                ", course=" + course +
                ", status='" + status + '\'' +
                ", time=" + time +
                '}';
    }
}
