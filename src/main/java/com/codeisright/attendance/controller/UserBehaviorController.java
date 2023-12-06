package com.codeisright.attendance.controller;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.cache.UserProfile;
import com.codeisright.attendance.service.StudentService;
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
@RequestMapping("/usr/{id}")
public class UserBehaviorController {
    private final TeacherService teacherService;
    private final StudentService studentService;

    @Autowired
    public UserBehaviorController(TeacherService teacherService, StudentService studentService) {
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    /**
     * 获取教师个人信息
     * @param id
     */
    @GetMapping("/teacher/getProfile")
    public UserProfile getTeacherProfile(@PathVariable String id) {
        Teacher target = teacherService.getTeacherById(id);
        UserProfile profile = new UserProfile();
        profile.setTeacher(target);
        return profile;
    }

    /**
     * 教师设置头像
     * @param id
     * @param avatar
     */
    @PostMapping("/teacher/setAvatar")
    public String setTeacherAvatar(@PathVariable String id, @RequestParam("avatar") MultipartFile avatar) {
        try {
            teacherService.saveAvatar(id, avatar.getBytes());
        }catch (Exception e){
            return "redirect:/uploadFailure";

        }
        return "redirect:/uploadSuccess";
    }

    /**
     * 上传失败页面
     * @return
     */
    @GetMapping("/uploadSuccess")
    public String uploadSuccess() {
        return "uploadSuccess";
    }

    /**
     * 获取教师头像
     * @param teacherId
     */
    @GetMapping("/getTeacherAvatar/{teacherId}")
    public ResponseEntity<Resource> getTeacherAvatar(@PathVariable String teacherId) {
        byte[] avatarBytes = teacherService.getProfileAvatar(teacherId);

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

    /**
     * 教师获取自己的班级列表
     * @param id
     * @return
     */
    @GetMapping("/teacher/classes")
    public List<Aclass> getTeacherClasses(@PathVariable String id) {
        return teacherService.getClassByTeacherId(id);
    }

    /**
     * 教师获取班级信息详情
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}")
    public Aclass getTeacherClass(@PathVariable String classId) {
        return teacherService.getClass(classId);
    }

    /**
     * 教师获取班级所属课程
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}/course")
    public Course getClassCourse(@PathVariable String classId) {
        return teacherService.getClassCourse(classId);
    }

    /**
     * 教师获取班级学生列表
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}/students")
    public List<Student> getTeacherClassStudents(@PathVariable String classId) {
        return teacherService.getClassStudents(classId);
    }

    /**
     * 教师获取班级某个学生的信息
     * @param studentId
     */
    @GetMapping("/teacher/classes/{classId}/students/{studentId}")
    public Student getStudent(@PathVariable String studentId) {
        return teacherService.getStudentInfo(studentId);
    }

    /**
     * 教师获取班级发布的签到记录
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}/meta")
    public List<AttendanceMeta> getAttendanceMeta(@PathVariable String classId) {
        return teacherService.getMetasByClassId(classId);
    }

    /**
     * 教师获取班级签到记录的详情
     * @param classId
     * @param metaId
     */
    @GetMapping("/teacher/classes/{classId}/meta/{metaId}")
    public AttendanceMeta getAttendanceMeta(@PathVariable String classId, @PathVariable String metaId) {
        return teacherService.getMetaByMetaId(metaId);
    }

    /**
     * 教师获取班级学生签到情况
     * @param classId
     * @param metaId
     */
    @GetMapping("/teacher/classes/{classId}/meta/{metaId}/list")  // 列出谁没签到，谁签到了
    public List<List<Student>> getAttendanceCircumstance(@PathVariable String classId, @PathVariable String metaId) {
        return teacherService.getAttendanceCircumstance(classId, metaId);
    }

    /**
     * 教师获取班级签到记录的Excel文档
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}/getExcel")
    public ResponseEntity<Resource> getAttendanceExcel(@PathVariable String classId) {
        // TODO: create an excel file and return
        return null;
    }

    /**
     * 教师创建班级
     * @param id
     * @param aclass
     */
    @PostMapping("/teacher/classes/{classId}")
    public Aclass addClass(@PathVariable String id, @RequestBody Aclass aclass) {
        return teacherService.addClass(id, aclass);
    }

    /**
     * 教师注册
     * @param teacher
     */
    @PostMapping("/teacher/register")
    public Teacher addTeacher(@RequestBody Teacher teacher) {
        return teacherService.addTeacher(teacher);
    }

    /**
     * 获取班级所有签到记录
     * @param classId
     * @param meta
     */
    @PostMapping("/teacher/classes/{classId}/meta")
    public AttendanceMeta attendanceMeta(@PathVariable String classId, @RequestBody AttendanceMeta meta) {
        return teacherService.announce(classId, meta);
    }

    /**
     * 更新教师信息
     * @param teacher
     */
    @PutMapping("/teacher")
    public Teacher updateTeacher(@RequestBody Teacher teacher) {
        return teacherService.updateTeacher(teacher);
    }

    /**
     * 上传头像
     * @param id
     * @param file
     */
    @PostMapping("/teacher/uploadAvatar")
    public String uploadAvator(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        try {
            teacherService.saveAvatar(id, file.getBytes());
        }catch (Exception e){
            return "redirect:/uploadFailure";

        }
        return "redirect:/uploadSuccess";
    }

    /**
     * 更新班级
     * @param aclass
     */
    @PutMapping("/teacher/classes/{classId}")
    public Aclass updateClass(@RequestBody Aclass aclass) {
        return teacherService.updateClass(aclass);
    }

    /**
     * 更新签到
     * @param meta
     */
    @PutMapping("/teacher/classes/{classId}/meta/{metaId}")
    public AttendanceMeta updateAttendanceMeta(@RequestBody AttendanceMeta meta) {
        return teacherService.updateAttendanceMeta(meta);
    }

    /**
     * 删除教师
     * @param id
     */
    @DeleteMapping("/teacher")
    public void deleteTeacher(@PathVariable String id) {
        teacherService.deleteTeacher(id);
    }

    /**
     * 删除班级
     * @param classId
     */
    @DeleteMapping("/teacher/classes/{classId}")
    public void deleteClass(@PathVariable String classId) {
        teacherService.deleteClass(classId);
    }

    /**
     * 删除签到
     * @param metaId
     */
    @DeleteMapping("/teacher/classes/{classId}/meta/{metaId}")
    public void deleteAttendanceMeta(@PathVariable String metaId) {
        teacherService.deleteAttendanceMeta(metaId);
    }

    /**
     * 删除班级学生
     * @param classId
     * @param studentId
     */
    @DeleteMapping("/teacher/classes/{classId}/student/{studentId}")
    public void deleteClassStudent(@PathVariable String classId, @PathVariable String studentId) {
        teacherService.deleteClassStudent(classId, studentId);
    }


    // The following are student APIs

    /**
     * 获取学生基本信息
     * @param id
     */
    @GetMapping("/student/getProfile")
    public UserProfile getStudentProfile(@PathVariable String id) {
        Student target = studentService.getStudentById(id);
        UserProfile profile = new UserProfile();
        profile.setStudent(target);
        return profile;
    }

    /**
     * 设置学生头像
     * @param id
     * @param avatar
     */
    @PostMapping("/student/setAvatar")
    public String setStudentAvatar(@PathVariable String id, @RequestParam("avatar") MultipartFile avatar) {
        try {
            studentService.saveAvatar(id, avatar.getBytes());
        }catch (Exception e){
            return "uploadFailure";
        }
        return "uploadSuccess";
    }

    /**
     * 获取学生头像
     * @param studentId
     */
    @GetMapping("/getStudentAvatar/{studentId}")
    public ResponseEntity<Resource> getStudentAvatar(@PathVariable String studentId) {
        byte[] avatarBytes = studentService.getProfileAvatar(studentId);

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

    /**
     * 获取某个班级所属的课程
     * @param classId
     */
    @GetMapping("/student/classes/{classId}/course")
    public Course getStudentClassCourse(@PathVariable String classId) {
        return studentService.getClassCourse(classId);
    }

    /**
     * 获取某个学生的所有班级
     * @param id
     */
    @GetMapping("/student/classes")
    public List<Aclass> getStudentClasses(@PathVariable String id) {
        return studentService.getClassByStudentId(id);
    }

    /**
     * 获取某个班级的所有学生
     * @param classId
     */
    @GetMapping("/student/classes/{classId}/students")
    public List<Student> getStudentClassStudents(@PathVariable String classId) {
        return studentService.getClassStudents(classId);
    }

    /**
     * 获取某个班级的信息
     * @param classId
     */
    @GetMapping("/student/classes/{classId}")
    public Aclass getStudentClass(@PathVariable String classId) {
        return studentService.getClassById(classId);
    }

    /**
     * 获取某个班级的老师
     * @param classId
     */
    @GetMapping("/student/classes/{classId}/teacher")
    public Teacher getStudentClassTeacher(@PathVariable String classId) {
        return studentService.getTeacherByClassId(classId);
    }

    /**
     * 获取某个班级的所有签到
     * @param classId
     */
    @GetMapping("/student/classes/{classId}/meta")
    public List<AttendanceMeta> getStudentAttendanceMeta(@PathVariable String classId) {
        return studentService.getMetasByClassId(classId);
    }

    /**
     * 获取某个学生在某个班级的某次签到的记录
     * @param id
     * @param metaId
     */
    @GetMapping("/student/classes/{classId}/meta/{metaId}")
    public Attendance getStudentClassMetaRecord(@PathVariable String id, @PathVariable String metaId) {
        return studentService.getAttendanceByStudentAndMeta(id, metaId);
    }

    /**
     * 签到码签到，插入Status为1的记录到Attendance表中
     * @param id
     * @param attendance
     */
    @PostMapping("/student/checkin1")
    public boolean checkin1(@PathVariable String id, @RequestBody Attendance attendance) {
        String classId = attendance.getAclass().getId();
        return studentService.doCheckin(id, classId, 1);
    }

    /**
     * 地理位置签到，将检查地理位置是否满足要求，满足则更新Attendance表中的Status为2和Location
     * @param id
     * @param attendance
     */
    @PutMapping("/student/checkin2")
    public boolean checkin2(@PathVariable String id, @RequestBody Attendance attendance) {
        String classId = attendance.getAclass().getId();
        Long Latitude = attendance.getLatitude();
        Long Longitude = attendance.getLongitude();
        return studentService.doLocation(id, classId, Latitude, Longitude);
    }

    /**
     * 二维码签到
     * @param id
     * @param attendance
     * @param QRCode
     */
    @PutMapping("/student/checkin3")
    public boolean checkin3(@PathVariable String id, @RequestBody Attendance attendance, @RequestParam String QRCode) {
        String classId = attendance.getAclass().getId();
        return studentService.doQR(id, classId, QRCode);
    }
}