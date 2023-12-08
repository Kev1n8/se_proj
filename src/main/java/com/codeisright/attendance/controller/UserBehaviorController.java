package com.codeisright.attendance.controller;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.service.StudentService;
import com.codeisright.attendance.service.TeacherService;
import com.codeisright.attendance.view.StudentInfo;
import com.codeisright.attendance.view.TeacherInfo;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/usr/{id}")
public class UserBehaviorController {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(UserBehaviorController.class);
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
    @PreAuthorize("#id == authentication.principal.username")
    public TeacherInfo getTeacherProfile(@PathVariable String id) {
        TeacherInfo target = teacherService.getTeacherInfoById(id);
        return target;
    }

    /**
     * 教师设置头像
     * @param id
     * @param avatar
     */
    @PostMapping("/teacher/setAvatar")
    @PreAuthorize("#id == authentication.principal.username")
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
     * @param id
     * @return
     */
    @GetMapping("/uploadSuccess")
    @PreAuthorize("#id == authentication.principal.username")
    public String uploadSuccess(@PathVariable String id) {
        return "uploadSuccess";
    }

    /**
     * 获取教师头像
     * @param teacherId
     */
    @GetMapping("/getTeacherAvatar/{teacherId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Resource> getTeacherAvatar(@PathVariable String id, @PathVariable String teacherId) {
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
    @PreAuthorize("#id == authentication.principal.username")
    public List<Aclass> getTeacherClasses(@PathVariable String id) {
        return teacherService.getClassByTeacherId(id);
    }

    /**
     * 教师获取班级信息详情
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public Aclass getTeacherClass(@PathVariable String id, @PathVariable String classId) {
        return teacherService.getClass(classId);
    }

    /**
     * 教师获取班级所属课程
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}/course")
    @PreAuthorize("#id == authentication.principal.username")
    public Course getClassCourse(@PathVariable String id, @PathVariable String classId) {
        return teacherService.getClassCourse(classId);
    }

    /**
     * 教师获取班级学生列表
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}/students")
    @PreAuthorize("#id == authentication.principal.username")
    public List<StudentInfo> getTeacherClassStudents(@PathVariable String id, @PathVariable String classId) {
        return teacherService.getClassStudents(classId);
    }

    /**
     * 教师获取班级某个学生的信息
     * @param id
     * @param studentId
     */
    @GetMapping("/teacher/classes/{classId}/students/{studentId}")
    @PreAuthorize("#id == authentication.principal.username")
    public StudentInfo getStudent(@PathVariable String id, @PathVariable String studentId) {
        return teacherService.getStudentInfo(studentId);
    }

    /**
     * 教师获取班级发布的签到记录
     * @param id
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}/meta")
    @PreAuthorize("#id == authentication.principal.username")
    public List<AttendanceMeta> getMetaList(@PathVariable String id, @PathVariable String classId) {
        return teacherService.getMetasByClassId(classId);
    }

    /**
     * 教师获取班级签到记录的详情
     * @param id
     * @param classId
     * @param metaId
     */
    @GetMapping("/teacher/classes/{classId}/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public AttendanceMeta getAttendanceMeta(@PathVariable String id, @PathVariable String classId, @PathVariable String metaId) {
        return teacherService.getMetaByMetaId(metaId);
    }

    /**
     * 教师获取班级学生签到情况
     * @param id
     * @param classId
     * @param metaId
     */
    @GetMapping("/teacher/classes/{classId}/meta/{metaId}/list")  // 列出谁没签到，谁签到了
    @PreAuthorize("#id == authentication.principal.username")
    public List<List<StudentInfo>> getAttendanceCircumstance(@PathVariable String id, @PathVariable String classId, @PathVariable String metaId) {
        return teacherService.getAttendanceCircumstance(classId, metaId);
    }

    /**
     * 教师获取班级签到记录的Excel文档
     * @param id
     * @param classId
     */
    @GetMapping("/teacher/classes/{classId}/getExcel")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Resource> getAttendanceExcel(@PathVariable String id, @PathVariable String classId) {
        byte[] excelBytes = teacherService.getClassExcel(classId);
        if (excelBytes == null || excelBytes.length == 0) {
            return ResponseEntity.noContent().build();
        }
        ByteArrayResource resource = new ByteArrayResource(excelBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(resource.contentLength());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    /**
     * 教师创建班级
     * @param id
     * @param aclass
     */
    @PostMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public Aclass addClass(@PathVariable String id, @RequestBody Aclass aclass) {
        return teacherService.addClass(id, aclass);
    }

    /**
     * 教师注册
     * @param id
     * @param teacher
     */
    @PostMapping("/teacher/register")
    @PreAuthorize("#id == authentication.principal.username")
    public Teacher addTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
        return teacherService.addTeacher(teacher);
    }

    /**
     * 获取班级所有签到记录
     * @param id
     * @param classId
     * @param meta
     */
    @PostMapping("/teacher/classes/{classId}/meta")
    @PreAuthorize("#id == authentication.principal.username")
    public AttendanceMeta attendanceMeta(@PathVariable String id, @PathVariable String classId, @RequestBody AttendanceMeta meta) {
        return teacherService.announce(classId, meta);
    }

    /**
     * 更新教师信息
     * @param teacher
     */
    @PutMapping("/teacher")
    @PreAuthorize("#id == authentication.principal.username")
    public Teacher updateTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
        return teacherService.updateTeacher(teacher);
    }

    /**
     * 上传头像
     * @param id
     * @param file
     */
    @PostMapping("/teacher/uploadAvatar")
    @PreAuthorize("#id == authentication.principal.username")
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
     * @param id
     * @param aclass
     */
    @PutMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public Aclass updateClass(@PathVariable String id, @RequestBody Aclass aclass) {
        return teacherService.updateClass(aclass);
    }

    /**
     * 更新签到
     * @param id
     * @param meta
     */
    @PutMapping("/teacher/classes/{classId}/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public AttendanceMeta updateAttendanceMeta(@PathVariable String id, @RequestBody AttendanceMeta meta) {
        return teacherService.updateAttendanceMeta(meta);
    }

    /**
     * 删除教师
     * @param id
     */
    @DeleteMapping("/teacher")
    @PreAuthorize("#id == authentication.principal.username")
    public void deleteTeacher(@PathVariable String id) {
        teacherService.deleteTeacher(id);
    }

    /**
     * 删除班级
     * @param id
     * @param classId
     */
    @DeleteMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public void deleteClass(@PathVariable String id, @PathVariable String classId) {
        teacherService.deleteClass(classId);
    }

    /**
     * 删除签到
     * @param id
     * @param metaId
     */
    @DeleteMapping("/teacher/classes/{classId}/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public void deleteAttendanceMeta(@PathVariable String id, @PathVariable String metaId) {
        teacherService.deleteAttendanceMeta(metaId);
    }

    /**
     * 删除班级学生
     * @param id
     * @param classId
     * @param studentId
     */
    @DeleteMapping("/teacher/classes/{classId}/student/{studentId}")
    @PreAuthorize("#id == authentication.principal.username")
    public void deleteClassStudent(@PathVariable String id, @PathVariable String classId, @PathVariable String studentId) {
        teacherService.deleteClassStudent(classId, studentId);
    }


    // The following are student APIs

    /**
     * 获取学生基本信息
     * @param id
     */
    @GetMapping("/student/getProfile")
    @PreAuthorize("#id == authentication.principal.username")
    public StudentInfo getStudentProfile(@PathVariable String id) {
        return studentService.getStudentInfoById(id);
    }

    /**
     * 设置学生头像
     * @param id
     * @param avatar
     */
    @PostMapping("/student/setAvatar")
    @PreAuthorize("#id == authentication.principal.username")
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
     * @param id
     * @param studentId
     */
    @GetMapping("/getStudentAvatar/{studentId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Resource> getStudentAvatar(@PathVariable String id, @PathVariable String studentId) {
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
     * @param id
     * @param classId
     */
    @GetMapping("/student/classes/{classId}/course")
    @PreAuthorize("#id == authentication.principal.username")
    public Course getStudentClassCourse(@PathVariable String id, @PathVariable String classId) {
        return studentService.getClassCourse(classId);
    }

    /**
     * 获取某个学生的所有班级
     * @param id
     */
    @GetMapping("/student/classes")
    @PreAuthorize("#id == authentication.principal.username")
    public List<Aclass> getStudentClasses(@PathVariable String id) {
        return studentService.getClassByStudentId(id);
    }

    /**
     * 获取某个班级的所有学生
     * @param classId
     */
    @GetMapping("/student/classes/{classId}/students")
    @PreAuthorize("#id == authentication.principal.username")
    public List<StudentInfo> getStudentClassStudents(@PathVariable String id, @PathVariable String classId) {
        return studentService.getClassStudents(classId);
    }

    /**
     * 获取某个班级的信息
     * @param classId
     */
    @GetMapping("/student/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public Aclass getStudentClass(@PathVariable String id, @PathVariable String classId) {
        return studentService.getClassById(classId);
    }

    /**
     * 获取某个班级的老师
     * @param classId
     */
    @GetMapping("/student/classes/{classId}/teacher")
    @PreAuthorize("#id == authentication.principal.username")
    public TeacherInfo getStudentClassTeacher(@PathVariable String id, @PathVariable String classId) {
        return studentService.getTeacherByClassId(classId);
    }

    /**
     * 获取某个班级的所有签到
     * @param classId
     */
    @GetMapping("/student/classes/{classId}/meta")
    @PreAuthorize("#id == authentication.principal.username")
    public List<AttendanceMeta> getStudentAttendanceMeta(@PathVariable String id, @PathVariable String classId) {
        return studentService.getMetasByClassId(classId);
    }

    /**
     * 获取某个学生在某个班级的某次签到的记录
     * @param id
     * @param metaId
     */
    @GetMapping("/student/classes/{classId}/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public Attendance getStudentClassMetaRecord(@PathVariable String id, @PathVariable String metaId) {
        return studentService.getAttendanceByStudentAndMeta(id, metaId);
    }

    /**
     * 签到码签到，插入Status为1的记录到Attendance表中
     * @param id
     * @param attendance
     */
    @PostMapping("/student/checkin1")
    @PreAuthorize("#id == authentication.principal.username")
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
    @PreAuthorize("#id == authentication.principal.username")
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
    @PreAuthorize("#id == authentication.principal.username")
    public boolean checkin3(@PathVariable String id, @RequestBody Attendance attendance, @RequestParam String QRCode) {
        String classId = attendance.getAclass().getId();
        return studentService.doQR(id, classId, QRCode);
    }
}