package com.codeisright.attendance.dto;

public class StudentDto {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String major;
    private String description;

    public StudentDto(String id, String name, int age, String gender, String major,
                      String description) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.major = major;
        this.description = description;
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

    public String getMajor() {
        return major;
    }

    public String getDescription() {
        return description;
    }
}
