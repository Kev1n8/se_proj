package com.codeisright.attendance.data;

import com.beust.ah.A;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
public class Teacher implements UserDetails {

    @Id
    private String id;

    private String name;

    private String codedpassword;

    private int age;

    private String gender;

    private String department;

    private String role;

    private boolean isExpired;

    protected Teacher() {}

    public Teacher(String teacherId, String teacherName, int age, String gender, String department, String codedPassword) {
        this.id = teacherId;
        this.name = teacherName;
        this.age = age;
        this.gender = gender;
        this.department = department;
        this.codedpassword = codedPassword;
        this.role = "ROLE_TEACHER";
        this.isExpired = false;
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

    public void setPassword(String codedPassword) {
        this.codedpassword = codedPassword;
    }

    public void setExpired(boolean isExpired) {
        this.isExpired = isExpired;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherId='" + id + '\'' +
                ", teacherName='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.<GrantedAuthority>of(new SimpleGrantedAuthority(this.role));
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
