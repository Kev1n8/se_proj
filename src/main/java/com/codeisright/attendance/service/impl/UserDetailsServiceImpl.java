package com.codeisright.attendance.service.impl;

import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.service.StudentService;
import com.codeisright.attendance.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final TeacherService teacherService;
    private final StudentService studentService;

    @Autowired
    public UserDetailsServiceImpl(TeacherService teacherService, StudentService studentService) {
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        logger.info("Finding id: " + username);
        Teacher teacher = teacherService.getTeacherById(username);
        if (teacher == null)
            return studentService.getStudentById(username);
        else
            return teacher;
    }
}
