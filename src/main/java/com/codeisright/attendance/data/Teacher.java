package com.codeisright.attendance.data;

import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name="teacherId", referencedColumnName="userId")
public class Teacher extends User{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private String teacherId;

    private String teacherName;

    private int age;

    private String gender;

    private String department;

    protected Teacher() {}

    public Teacher(String teacherId, String teacherName, String department) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.department = department;
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
}
