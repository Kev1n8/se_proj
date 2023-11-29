package com.codeisright.attendance.data;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private String courseCode;

    private String courseName;

    private String courseDescription;

    protected Course() {}

    public Course(String courseId, String courseCode, String courseName, Teacher teacher, LocalDateTime classTime) {
        this.courseCode = courseId;
        this.courseName = courseName;
    }

    public String getId() {
        return courseCode;
    }

    public void setId(String courseId) {
        this.courseCode = courseId;
    }

    public String getName() {
        return courseName;
    }

    public void setName(String courseName) {
        this.courseName = courseName;
    }


    public void setTime(LocalDateTime classTime) {
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
                "courseId='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseDescription='" + courseDescription + '\'' +
                '}';
    }
}
