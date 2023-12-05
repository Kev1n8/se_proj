package com.codeisright.attendance.cache;


import com.codeisright.attendance.data.Student;
import com.codeisright.attendance.data.Teacher;

public class UserProfile {
    private String id;

    private String name;

    private int age;

    private String gender;

    private String department;

    private String major;

    private String theclass;

    private String role;


    public UserProfile() {
        this.id = "";
        this.name = "";
        this.age = -1;
        this.gender = "";
        this.department = "";
        this.major = "";
        this.theclass = "";
        this.role = "";
    }

    public UserProfile(String id, String name, int age, String gender, String department, String major, String theclass,
                       String role) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.department = department;
        this.major = major;
        this.theclass = theclass;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public void setTheclass(String theclass) {
        this.theclass = theclass;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setTeacher(Teacher teacher) {
        this.gender = teacher.getGender();
        this.age = teacher.getAge();
        this.department = teacher.getDepartment();
        this.name = teacher.getName();
        this.id = teacher.getUsername();
        this.role = "teacher";
    }

    public void setStudent(Student student) {
        this.gender = student.getGender();
        this.age = student.getAge();
        this.major = student.getMajor();
        this.theclass = student.getTheclass();
        this.name = student.getName();
        this.id = student.getUsername();
        this.role = "student";
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", department='" + department + '\'' +
                ", major='" + major + '\'' +
                ", theclass='" + theclass + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
