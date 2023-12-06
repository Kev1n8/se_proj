package com.codeisright.attendance.controller;

import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.service.TeacherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {
    private AuthenticationManager authenticationManager;
    private TeacherService teacherService;

    @Autowired
    public LoginController(AuthenticationManager authenticationManager, TeacherService teacherService) {
        this.authenticationManager = authenticationManager;
        this.teacherService = teacherService;
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        try {
            Authentication auth = authenticationManager.authenticate(token);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());
            return "redirect:/hello";
        }catch (Exception e) {
            return "login?error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @PostMapping("/register/teacher")
    public Teacher register(@RequestParam Teacher teacher) {
        try {
            return teacherService.addTeacher(teacher);
        } catch (Exception e) {
            return null;
        }
    }

    @GetMapping("/register/teacher")
    public String getTeacher(Model model) {
        return "hello, please register";
    }


}
