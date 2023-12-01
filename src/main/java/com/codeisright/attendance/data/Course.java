package com.codeisright.attendance.data;

import jakarta.persistence.*;

@Entity
public class Course {

    @Id
    private String code;

    private String name;

    private String description;

    protected Course() {}

    public Course(String code, String name) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return code;
    }

    public void setId(String courseId) {
        this.code = courseId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String courseCode) {
        this.code = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String courseName) {
        this.name = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String courseDescription) {
        this.description = courseDescription;
    }

    @Override
    public String toString() {
        return "Course{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
