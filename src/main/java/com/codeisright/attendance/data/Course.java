package com.codeisright.attendance.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private String courseId;

    private String courseNo;

    private String courseName;

    @ManyToOne
    @JoinColumn(name="teacherId")
    private Teacher teacher;

    private LocalDateTime classTime;

    private String location;

    protected Course() {}

    public Course(String courseId, String courseNo, String courseName, Teacher teacher, LocalDateTime classTime,
                  String location) {
        this.courseId = courseId;
        this.courseNo = courseNo;
        this.courseName = courseName;
        this.teacher = teacher;
        this.classTime = classTime;
        this.location = location;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(String courseNo) {
        this.courseNo = courseNo;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public LocalDateTime getClassTime() {
        return classTime;
    }

    public void setClassTime(LocalDateTime classTime) {
        this.classTime = classTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseNo='" + courseNo + '\'' +
                ", courseName='" + courseName + '\'' +
                ", teacher=" + teacher +
                ", classTime=" + classTime +
                ", location='" + location + '\'' +
                '}';
    }
}
