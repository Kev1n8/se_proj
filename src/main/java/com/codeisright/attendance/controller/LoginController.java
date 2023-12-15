package com.codeisright.attendance.controller;

import com.codeisright.attendance.cache.UserDto;
import com.codeisright.attendance.data.Student;
import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.service.StudentService;
import com.codeisright.attendance.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@RestController
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final AuthenticationManager authenticationManager;
    private final TeacherService teacherService;
    private final StudentService studentService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, TeacherService teacherService, StudentService studentService) {
        this.authenticationManager = authenticationManager;
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    /**
     * login and return token
     * @param dto user info
     * @param request  http request
     * @return role
     */
    @PostMapping("/login")
    public String login(@RequestBody UserDto dto, HttpServletResponse response, HttpServletRequest request) {
        logger.info("login request received" + dto.toString());
        String id = dto.getId();
        String password = dto.getPassword();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(id, password);
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 10 days
                .signWith(SignatureAlgorithm.HS512, "secret".getBytes())
                .compact();
        response.addHeader("Authorization", "Bearer " + token);
        logger.info("login success");
        return userDetails.getAuthorities().toArray()[0].toString();
    }

    @GetMapping("/login")
    public String loginGet(){
        return "test";
    }

    /**
     * logout
     */
    @GetMapping("/logout")
    public void logout(HttpServletRequest request) {
        logger.info("logout request received");
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    @PostMapping("/register/teacher")
    public Teacher registerTeacher(@RequestBody Teacher teacher) {
        try {
            logger.info("registering teacher: " + teacher.getUsername());
            return teacherService.registerTeacher(teacher);
        } catch (Exception e) {
            logger.info("registering teacher failed");
            return null;
        }
    }

    @GetMapping("/register/teacher")
    public String getRegTeacher() {
        return "hello, please register";
    }

    @PostMapping("/register/student")
    public Student registerStudent(@RequestBody Student student) {
        try {
            logger.info("registering student: " + student.getUsername());
            return studentService.registerStudent(student);
        } catch (Exception e) {
            logger.info("registering student failed");
            return null;
        }
    }
}
