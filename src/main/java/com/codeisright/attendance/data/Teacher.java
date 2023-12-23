package com.codeisright.attendance.data;

import com.codeisright.attendance.view.TeacherInfo;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
public class Teacher implements UserDetails {

    @Id
    private String id;

    private String name;

    private String password;

    private int age;

    private String gender;

    private String department;


    protected Teacher() {}

    public Teacher(String id, String password) {
        this.id = id;
        this.password = password;
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
        this.password = codedPassword;
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
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        GrantedAuthority authority1 = new SimpleGrantedAuthority("ROLE_TEACHER");
        return List.of(authority, authority1);
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

    public TeacherInfo toTeacherInfo(){
        return new TeacherInfo() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getAge() {
                return age;
            }

            @Override
            public String getGender() {
                return gender;
            }

            @Override
            public String getDepartment() {
                return department;
            }
        };
    }
}
