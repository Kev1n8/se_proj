package com.codeisright.attendance.dto;

public class TeacherDto {
    private String id;

    private String name;

    private int age;

    private String gender;

    private String department;

    public TeacherDto(String id, String name, int age, String gender, String department) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getDepartment() {
        return department;
    }
}
