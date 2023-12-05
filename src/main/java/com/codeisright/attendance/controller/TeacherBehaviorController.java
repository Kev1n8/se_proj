package com.codeisright.attendance.controller;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.cache.UserProfile;
import com.codeisright.attendance.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/teacher/{id}")
public class TeacherBehaviorController {
    private final TeacherService teacherService;

    @Autowired
    public TeacherBehaviorController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public UserProfile getTeacherProfile(@PathVariable String id) {
        Teacher target = teacherService.getTeacherById(id);
        UserProfile profile = new UserProfile();
        profile.setTeacher(target);
        return profile;
    }

    @GetMapping("/getAvatar")
    public ResponseEntity<Resource> getTeacherAvatar(@PathVariable String id) {
        byte[] avatarBytes = teacherService.getProfileAvatar(id);

        if (avatarBytes == null || avatarBytes.length == 0) {
            // 如果没有头像数据，你可能需要返回一个默认图像或者空响应
            return ResponseEntity.noContent().build();
        }

        ByteArrayResource resource = new ByteArrayResource(avatarBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(resource.contentLength());

        // 根据实际的图像类型设置Content-type
        // 这里简单演示，你可能需要根据实际情况从数据库或文件名中获取图像类型
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @PostMapping("/setAvatar")
    public String setTeacherAvatar(@PathVariable String id, @RequestParam("avatar") MultipartFile avatar) {
        try {
            teacherService.saveAvatar(id, avatar.getBytes());
        }catch (Exception e){
            return "redirect:/uploadFailure";

        }
        return "redirect:/uploadSuccess";
    }

    @GetMapping("/student/{studentId}/avatar")
    public ResponseEntity<Resource> getStudentAvatar(@PathVariable String studentId) {
        byte[] avatarBytes = teacherService.getProfileAvatar(studentId);

        if (avatarBytes == null || avatarBytes.length == 0) {
            // 如果没有头像数据，你可能需要返回一个默认图像或者空响应
            return ResponseEntity.noContent().build();
        }

        ByteArrayResource resource = new ByteArrayResource(avatarBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(resource.contentLength());

        // 根据实际的图像类型设置Content-type
        // 这里简单演示，你可能需要根据实际情况从数据库或文件名中获取图像类型
        headers.setContentType(MediaType.IMAGE_JPEG);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/classes")
    public List<Aclass> getTeacherClasses(@PathVariable String id) {
        return teacherService.getClasses(id);
    }

    @GetMapping("/classes/{classId}")
    public Aclass getTeacherClass(@PathVariable String classId) {
        return teacherService.getClass(classId);
    }

    @GetMapping("/classes/{classId}/students")
    public List<Student> getTeacherClassStudents(@PathVariable String classId) {
        return teacherService.getClassStudents(classId);
    }

    @GetMapping("/classes/{classId}/students/{studentId}")
    public Student getStudent(@PathVariable String studentId) {
        return teacherService.getStudentInfo(studentId);
    }

    @GetMapping("/classes/{classId}/metalist")
    public List<Attendance> getAttendanceMeta(@PathVariable String classId) {
        return teacherService.getAttendanceByClassId(classId);
    }

    @GetMapping("/classes/{classId}/meta/{metaId}")
    public AttendanceMeta getAttendanceMeta(@PathVariable String classId, @PathVariable String metaId) {
        return teacherService.getAttendanceMeta(metaId);
    }

    @GetMapping("/classes/{classId}/meta/{metaId}/list")  // 列出谁没签到，谁签到了
    public List<List<Student>> getAttendanceCircumstance(@PathVariable String classId, @PathVariable String metaId) {
        return teacherService.getAttendanceCircumstance(classId, metaId);
    }

    @GetMapping("/classes/{classId}/getExcel")
    public ResponseEntity<Resource> getAttendanceExcel(@PathVariable String classId) {
        // TODO: create an excel file and return
        return null;
    }


    @PostMapping("/classes/{classId}")
    public Aclass addClass(@PathVariable String id, @RequestBody Aclass aclass) {
        return teacherService.addClass(id, aclass);
    }

    @PostMapping()
    public Teacher addTeacher(@RequestBody Teacher teacher) {
        return teacherService.addTeacher(teacher);
    }

    @PostMapping("/classes/{classId}/meta")
    public AttendanceMeta attendanceMeta(@PathVariable String classId, @RequestBody AttendanceMeta meta) {
        return teacherService.announce(classId, meta);
    }

    @PutMapping()
    public Teacher updateTeacher(@RequestBody Teacher teacher) {
        return teacherService.updateTeacher(teacher);
    }

    @PostMapping("/uploadAvatar")
    public String uploadAvator(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            teacherService.saveAvatar(id, file.getBytes());
        }catch (Exception e){
            return "redirect:/uploadFailure";

        }
        return "redirect:/uploadSuccess";
    }

    @PutMapping("/classes/{classId}")
    public Aclass updateClass(@RequestBody Aclass aclass) {
        return teacherService.updateClass(aclass);
    }

    @PutMapping("/classes/{classId}/meta/{metaId}")
    public AttendanceMeta updateAttendanceMeta(@RequestBody AttendanceMeta meta) {
        return teacherService.updateAttendanceMeta(meta);
    }

    @DeleteMapping()
    public void deleteTeacher(@PathVariable String id) {
        teacherService.deleteTeacher(id);
    }

    @DeleteMapping("/classes/{classId}")
    public void deleteClass(@PathVariable String id, @PathVariable String classId) {
        teacherService.deleteClass(classId);
    }

    @DeleteMapping("/classes/{classId}/meta/{metaId}")
    public void deleteAttendanceMeta(@PathVariable String metaId) {
        teacherService.deleteAttendanceMeta(metaId);
    }

    @DeleteMapping("/classes/{classId}/student/{studentId}")
    public void deleteClassStudent(@PathVariable String classId, @PathVariable String studentId) {
        teacherService.deleteClassStudent(classId, studentId);
    }
}