package com.codeisright.attendance.data;

import jakarta.persistence.*;

@Entity
public class Aclass {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    private String title;

    private String description;

    private int grade;

    @ManyToOne
    @JoinColumn(name="courseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name="teacherId")
    private Teacher teacher;

    protected Aclass() {}

    public Aclass(String title, String description, int grade, Course course, Teacher teacher) {
        this.title = title;
        this.description = description;
        this.grade = grade;
        this.course = course;
        this.teacher = teacher;
    }

    public String getId() {
        return id;
    }

    public void setId(String classId) {
        this.id = classId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Aclass{" +
                "classId='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", grade=" + grade +
                ", course=" + course +
                ", teacher=" + teacher +
                '}';
    }
}
