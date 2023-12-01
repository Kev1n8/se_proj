package com.codeisright.attendance.data;

import jakarta.persistence.*;

@Entity
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @ManyToOne
    @JoinColumn(name="studentId")
    private Student student;

    @ManyToOne
    @JoinColumn(name="classId")
    private Aclass aclass;

    public Enrollment() {}

    public Enrollment(Student student, Aclass aclass) {
        this.student = student;
        this.aclass = aclass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Aclass getAclass() {
        return aclass;
    }

    public void setAclass(Aclass aclass) {
        this.aclass = aclass;
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "id='" + id + '\'' +
                ", student=" + student +
                ", aclass=" + aclass +
                '}';
    }
}
