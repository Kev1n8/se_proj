package com.codeisright.attendance.service;

import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.exception.EntityNotFoundException;
import com.codeisright.attendance.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;
//    private PasswordEncoder passwordEncoder;

//    @Configuration
//    class PasswordEncoder {
//        @Bean
//        public BCryptPasswordEncoder bCryptPasswordEncoder() {
//            return new BCryptPasswordEncoder();
//        }
//    }

//    @Autowired
//    public TeacherService(TeacherRepository teacherRepository, PasswordEncoder passwordEncoder) {
//        this.teacherRepository = teacherRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(String id) {
        return teacherRepository.findById(id).orElse(null);
    }

    public Teacher addTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Teacher teacher) {
        Teacher existingTeacher = teacherRepository.findById(teacher.getId()).orElseThrow(() -> new EntityNotFoundException("Teacher not found with ID: "+teacher.getId()));
        existingTeacher.setName(teacher.getName());
        existingTeacher.setAge(teacher.getAge());
        existingTeacher.setGender(teacher.getGender());
        existingTeacher.setDepartment(teacher.getDepartment());
        return teacherRepository.save(existingTeacher);
    }

    public void deleteTeacher(String id) {
        teacherRepository.deleteById(id);
    }

    public List<Teacher> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
    }

    public Teacher registerTeacher(String teacherId, String teacherName, int age, String gender, String department, String password) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher != null) {
            return null;
        }
        teacher = new Teacher(teacherId, teacherName, age, gender, department, password);
        return addTeacher(teacher);
    }

    public Teacher loginTeacher(String teacherId, String password) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            return null;
        }
        if (password.matches(teacher.getCodedpassword())) {
            return teacher;
        }
        return null;
    }
}
