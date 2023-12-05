package com.codeisright.attendance.data;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Entity
public class Student implements UserDetails {

    @Id
    private String id;

    private String codedpassword;
    private String name;
    private int age;
    private String gender;
    private String major;
    private String theclass;
    protected Student() {}

    public Student(String id, String studentName, String codedpassword, int age, String gender, String major, String studentClass) {
        this.id = id;
        this.name = studentName;
        this.age = age;
        this.gender = gender;
        this.major = major;
        this.theclass = studentClass;
        this.codedpassword = codedpassword;
    }
    public void setId(String stuId) {
        this.id = stuId;
    }

    public String getCodedpassword() {
        return codedpassword;
    }

    public void setCodedpassword(String codedpassword) {
        this.codedpassword = codedpassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String stuName) {
        this.name = stuName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getTheclass() {
        return theclass;
    }

    public void setTheclass(String stuClass) {
        this.theclass = stuClass;
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
                "studentId='" + id + '\'' +
                ", studentName='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", major='" + major + '\'' +
                ", studentClass='" + theclass + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.codedpassword;
    }

    @Override
    public String getUsername() {
        return this.id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
