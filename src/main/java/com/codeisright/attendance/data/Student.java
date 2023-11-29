package com.codeisright.attendance.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private String studentId;

    private String studentName;

    private int age;

    private String gender;

    private String major;

    private String studentClass;

    protected Student() {}

    public Student(String studentId, String studentName, int age, String gender, String major, String studentClass) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.age = age;
        this.gender = gender;
        this.major = major;
        this.studentClass = studentClass;
    }

    public String getId() {
        return studentId;
    }

    public void setId(String stuId) {
        this.studentId = stuId;
    }

    public String getName() {
        return studentName;
    }

    public void setName(String stuName) {
        this.studentName = stuName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String stuClass) {
        this.studentClass = stuClass;
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

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", major='" + major + '\'' +
                ", studentClass='" + studentClass + '\'' +
                '}';
    }
}
