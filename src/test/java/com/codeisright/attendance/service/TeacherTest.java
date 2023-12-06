package com.codeisright.attendance.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class TeacherTest {
    private final TeacherService teacherService;

    @Autowired
    public TeacherTest(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @Test
    public void test() {

        assertEquals(1, 1);
    }
}
