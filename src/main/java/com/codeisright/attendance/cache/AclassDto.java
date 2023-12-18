package com.codeisright.attendance.cache;

import com.codeisright.attendance.data.Aclass;

import java.util.List;

public class AclassDto {
    private String title;

    private String description;

    private int grade;

    private String courseId;

    private String teacherId;

    public AclassDto(Aclass aclass) {
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

    public static AclassDto toDto(Aclass item){
        return new AclassDto(item);
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
