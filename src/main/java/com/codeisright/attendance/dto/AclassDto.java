package com.codeisright.attendance.dto;

import com.codeisright.attendance.data.Aclass;

import java.util.List;
import java.util.Map;

public class AclassDto {
    private final String title;

    private final String description;

    private final int grade;

    private final String courseId;

    private final String teacherId;

    public AclassDto(String title, String description, int grade, String courseId, String teacherId) {
        this.title = title;
        this.description = description;
        this.grade = grade;
        this.courseId = courseId;
        this.teacherId = teacherId;
    }

    public AclassDto(Aclass aclass) {
        if (aclass == null) {
            this.title = "";
            this.description = "";
            this.grade = 0;
            this.courseId = "";
            this.teacherId = "";
            return;
        }
        this.title = aclass.getTitle();
        this.description = aclass.getDescription();
        this.grade = aclass.getGrade();
        this.courseId = aclass.getCourse().getId();
        this.teacherId = aclass.getTeacher().getUsername();
    }

    public static List<AclassDto> Convert(List<Aclass> ls){
        List<AclassDto> res = new java.util.ArrayList<>();
        for (Aclass item : ls) {
            res.add(new AclassDto(item));
        }
        return res;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getGrade() {
        return grade;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTeacherId() {
        return teacherId;
    }
}
