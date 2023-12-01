package com.codeisright.attendance.data;

import jakarta.persistence.*;

@Entity
public class Teacher {

    @Id
    private String id;

    private String name;

    private String codedpassword;

    private int age;

    private String gender;

    private String department;

    protected Teacher() {}

    public Teacher(String teacherId, String teacherName, int age, String gender, String department, String codedPassword) {
        this.id = teacherId;
        this.name = teacherName;
        this.age = age;
        this.gender = gender;
        this.department = department;
        this.codedpassword = codedPassword;
    }

    public String getId() {
        return id;
    }

    public void setId(String teaId) {
        this.id = teaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String teaName) {
        this.name = teaName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String teaDept) {
        this.department = teaDept;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCodedpassword() {
        return codedpassword;
    }

    public void setCodedpassword(String codedPassword) {
        this.codedpassword = codedPassword;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId='" + id + '\'' +
                ", teacherName='" + name + '\'' +
                ", codedPassword='" + codedpassword + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
