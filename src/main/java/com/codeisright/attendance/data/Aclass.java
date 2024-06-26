package com.codeisright.attendance.data;

import com.codeisright.attendance.dto.AclassDto;
import com.codeisright.attendance.view.AclassInfo;
import com.codeisright.attendance.view.TeacherInfo;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
public class Aclass {
    @Id
////    @GenericGenerator(name = "UUID", type = org.hibernate.id.uuid.UuidGenerator.class)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "uuid2")
    private String id;

    private String title;

    private String description;

    private int grade;

    @ManyToOne
    @JoinColumn(name="courseId")
    private Course course;

    @ManyToOne
    @JoinColumn(name="teacherId")
    private Teacher teacher;

    public Aclass() {}

    public void setClass(AclassDto aclass, Course course, Teacher teacher) {
        // auto-generated id
        this.id = null;
        this.title = aclass.getTitle();
        this.description = aclass.getDescription();
        this.grade = aclass.getGrade();
        this.course = course;
        this.teacher = teacher;
    }

    public String getId() {
        return id;
    }

    public void setId(String classId) {
        this.id = classId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public TeacherInfo getTeacherInfo() {
        return new TeacherInfo() {
            @Override
            public String getId() {
                return teacher.getUsername();
            }

            @Override
            public String getName() {
                return teacher.getName();
            }

            @Override
            public int getAge() {
                return teacher.getAge();
            }

            @Override
            public String getGender() {
                return teacher.getGender();
            }

            @Override
            public String getDepartment() {
                return teacher.getDepartment();
            }
        };
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Aclass{" +
                "classId='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", grade=" + grade +
                ", course=" + course +
                ", teacher=" + teacher +
                '}';
    }

    public AclassInfo toAclassInfo() {
        return new AclassInfo() {
            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getDescription() {
                return description;
            }

            @Override
            public int getGrade() {
                return grade;
            }

            @Override
            public String getCourseId() {
                return course.getId();
            }

            @Override
            public String getTeacherId() {
                return teacher.getUsername();
            }
        };
    }
}
