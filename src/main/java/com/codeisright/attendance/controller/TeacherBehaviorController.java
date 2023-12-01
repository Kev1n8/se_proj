package com.codeisright.attendance.controller;


import com.codeisright.attendance.data.Aclass;
import com.codeisright.attendance.data.Student;
import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.cache.UserProfile;
import com.codeisright.attendance.service.StudentService;
import com.codeisright.attendance.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/teacher/{id}")
public class TeacherBehaviorController {
    private final TeacherService teacherService;

    @Autowired
    public TeacherBehaviorController(TeacherService teacherService){
        this.teacherService = teacherService;
    }

    @GetMapping
    public UserProfile getTeacherProfile(@PathVariable String id){
        Teacher target = teacherService.getTeacherById(id);
        UserProfile profile = new UserProfile();
        profile.setTeacher(target);
        return profile;
    }

    @GetMapping("/classes")
    public List<Aclass> getTeacherClasses(@PathVariable String id){
        return teacherService.getClasses(id);
    }

    @GetMapping("/classes/{classId}")
    public Aclass getTeacherClass(@PathVariable String classId){
        return teacherService.getClass(classId);
    }

    @GetMapping("/classes/{classId}/students")
    public List<Student> getTeacherClassStudents(@PathVariable String classId){
        return teacherService.getClassStudents(classId);
    }

    @GetMapping("/classes/{classId}/students/{studentId}")
    public Student getStudent(@PathVariable String studentId){
        return teacherService.getStudentInfo(studentId);
    }
}
