package com.codeisright.attendance.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private String courseId;

    private String courseCode;

    private String courseName;

    private String courseDescription;

    @ManyToOne
    @JoinColumn(name="teacherId")
    private Teacher teacher;

    private LocalDateTime classTime;

    private String location;

    protected Course() {}

    public Course(String courseId, String courseCode, String courseName, Teacher teacher, LocalDateTime classTime,
                  String location) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.teacher = teacher;
        this.classTime = classTime;
        this.location = location;
    }

    public String getId() {
        return courseId;
    }

    public void setId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseNo) {
        this.courseCode = courseNo;
    }

    public String getName() {
        return courseName;
    }

    public void setName(String courseName) {
        this.courseName = courseName;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public LocalDateTime getTime() {
        return classTime;
    }

    public void setTime(LocalDateTime classTime) {
        this.classTime = classTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId='" + courseId + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                ", teacher=" + teacher +
                ", classTime=" + classTime +
                ", location='" + location + '\'' +
                '}';
    }
}
