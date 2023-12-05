package com.codeisright.attendance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/bye")
    public String bye() {
        return "Bye World!";
    }
}
