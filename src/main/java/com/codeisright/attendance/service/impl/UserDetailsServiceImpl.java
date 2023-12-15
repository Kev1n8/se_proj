package com.codeisright.attendance.service.impl;

import com.codeisright.attendance.data.Jwt;
import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.repository.JwtRepository;
import com.codeisright.attendance.service.StudentService;
import com.codeisright.attendance.service.TeacherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final JwtRepository jwtRepository;

    @Autowired
    public UserDetailsServiceImpl(TeacherService teacherService, StudentService studentService, JwtRepository jwtRepository) {
        this.teacherService = teacherService;
        this.studentService = studentService;
        this.jwtRepository = jwtRepository;
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

    public Collection<? extends GrantedAuthority> getAuthoritiesById(String id) {
        Teacher teacher = teacherService.getTeacherById(id);
        if (teacher==null)
            return studentService.getStudentById(id).getAuthorities();
        else
            return teacher.getAuthorities();
    }

    public void saveJwt(String id, String jwt) {
        logger.info("Saving jwt for id: " + id);
        jwtRepository.save(new Jwt(id, jwt));
    }

    public void deleteJwt(String id) {
        jwtRepository.deleteById(id);
    }

    public String getJwt(String id) {
        return jwtRepository.findById(id).orElseThrow().getToken();
    }
}
