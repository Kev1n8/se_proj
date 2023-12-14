package com.codeisright.attendance.data;

import com.codeisright.attendance.view.StudentInfo;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
public class Student implements UserDetails {

    @Id
    private String id;

    private String password;
    private String name;
    private int age;
    private String gender;
    private String major;
    private String description;
    protected Student() {}

    public String getId() {
        return null;
    }

    public void setId(String stuId) {
        this.id = stuId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String stuClass) {
        this.description = stuClass;
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
                ", studentClass='" + description + '\'' +
                '}';
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        GrantedAuthority authorities = new SimpleGrantedAuthority("ROLE_USER");
        GrantedAuthority authorities1 = new SimpleGrantedAuthority("ROLE_STUDENT");
        return List.of(authorities, authorities1);
    }

    @Override
    public String getPassword() {
        return this.password;
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

    public StudentInfo toStudentInfo(){
        return new StudentInfo() {
            @Override
            public String getId() {
                return Student.this.getId();
            }

            @Override
            public String getName() {
                return Student.this.getName();
            }

            @Override
            public int getAge() {
                return Student.this.getAge();
            }

            @Override
            public String getGender() {
                return Student.this.getGender();
            }

            @Override
            public String getMajor() {
                return Student.this.getMajor();
            }

            @Override
            public String getDescription() {
                return Student.this.getDescription();
            }
        };
    }
}
