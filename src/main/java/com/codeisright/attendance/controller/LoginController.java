package com.codeisright.attendance.controller;

import com.codeisright.attendance.cache.UserDto;
import com.codeisright.attendance.data.Student;
import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.service.StudentService;
import com.codeisright.attendance.service.TeacherService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    @PostMapping("/login")
    public String login(@RequestBody UserDto dto, HttpServletRequest request) {
        logger.info("login request received" + dto.toString());
        String id = dto.getId();
        String password = dto.getPassword();
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(id, password);
        try {
            logger.info("username: " + id + "pairing...");
            Authentication auth = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
            logger.info("username: " + id + "pairing successfully");
            return "hello";
        }catch (Exception e) {
            logger.info("username: " + id + "pairing failed");
            return "login?error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        try {
            session.invalidate();
            logger.info("logout successfully");
        } catch (Exception e) {
            logger.info("logout failed");
            return "logout?error";
        }
        return "redirect:/login";
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
