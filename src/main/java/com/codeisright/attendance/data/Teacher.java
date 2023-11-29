package com.codeisright.attendance.data;

import jakarta.persistence.*;

@Entity
public class Teacher {

    @Id
    private String teacherId;

    private String teacherName;

    private String codedPassword;

    private int age;

    private String gender;

    private String department;

    protected Teacher() {}

    public Teacher(String teacherId, String teacherName, int age, String gender, String department, String codedPassword) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.age = age;
        this.gender = gender;
        this.department = department;
        this.codedPassword = codedPassword;
    }

    public String getId() {
        return teacherId;
    }

    public void setId(String teaId) {
        this.teacherId = teaId;
    }

    public String getName() {
        return teacherName;
    }

    public void setName(String teaName) {
        this.teacherName = teaName;
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

    public String getCodedPassword() {
        return codedPassword;
    }

    public void setCodedPassword(String codedPassword) {
        this.codedPassword = codedPassword;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId='" + teacherId + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", codedPassword='" + codedPassword + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
