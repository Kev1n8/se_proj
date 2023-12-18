package com.codeisright.attendance.controller;

import com.codeisright.attendance.cache.AclassDto;
import com.codeisright.attendance.cache.MetaDto;
import com.codeisright.attendance.data.*;
import com.codeisright.attendance.service.StudentService;
import com.codeisright.attendance.service.TeacherService;
import com.codeisright.attendance.view.StudentInfo;
import com.codeisright.attendance.view.TeacherInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usr/{id}")
public class UserBehaviorController {
    private final Logger logger = LoggerFactory.getLogger(UserBehaviorController.class);
    private final TeacherService teacherService;
    private final StudentService studentService;

    @Autowired
    public UserBehaviorController(TeacherService teacherService, StudentService studentService) {
        this.teacherService = teacherService;
        this.studentService = studentService;
    }

    /**
     * 获取教师个人信息
     *
     * @param id 教师id
     */
    @GetMapping("/teacher/getProfile")
    @PreAuthorize("#id == authentication.principal.username")
    public TeacherInfo getTeacherProfile(@PathVariable String id) {
        logger.info("Get teacher profile request received");
        return teacherService.getTeacherInfoById(id);
    }

    /**
     * 教师设置头像
     *
     * @param id     教师id
     * @param avatar 头像文件
     */
    @PostMapping("/teacher/setAvatar")
    @PreAuthorize("#id == authentication.principal.username")
    public String setTeacherAvatar(@PathVariable String id, @RequestParam("avatar") MultipartFile avatar) {
        logger.info("Set teacher avatar request received");
        try {
            teacherService.saveAvatar(id, avatar.getBytes());
        } catch (Exception e) {
            return "redirect:/uploadFailure";

        }
        return "redirect:/uploadSuccess";
    }

    //TODO：各种重定向，返回值可能要改

    /**
     * 上传失败页面
     *
     * @param id 教师id
     * @return 上传成功页面
     */
    @GetMapping("/uploadSuccess")
    @PreAuthorize("#id == authentication.principal.username")
    public String uploadSuccess(@PathVariable String id) {
        return "uploadSuccess";
    }

    /**
     * 获取教师头像
     *
     * @param teacherId 教师id
     */
    @GetMapping("/getTeacherAvatar/{teacherId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Resource> getTeacherAvatar(@PathVariable String id, @PathVariable String teacherId) {
        logger.info("Get teacher avatar request received");
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
     *
     * @param id 教师id
     * @return 班级列表
     */
    @GetMapping("/teacher/classes")
    @PreAuthorize("#id == authentication.principal.username")
    public List<Aclass> getTeacherClasses(@PathVariable String id) {
        logger.info("Get teacher classes request received");
        return teacherService.getClassByTeacherId(id);
    }

    /**
     * 教师获取班级信息详情
     *
     * @param classId 班级id
     */
    @GetMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public Aclass getTeacherClass(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get teacher class request received");
        return teacherService.getClass(classId);
    }

    /**
     * 教师获取班级所属课程
     *
     * @param classId 班级id
     */
    @GetMapping("/teacher/classes/{classId}/course")
    @PreAuthorize("#id == authentication.principal.username")
    public Course getClassCourse(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get class course request received");
        return teacherService.getClassCourse(classId);
    }

    /**
     * 教师获取班级学生列表
     *
     * @param classId 班级id
     * @return 学生列表
     */
    @GetMapping("/teacher/classes/{classId}/students")
    @PreAuthorize("#id == authentication.principal.username")
    public Page<StudentInfo> getTeacherClassStudents(@PathVariable String id, @PathVariable String classId,
                                                     @RequestParam(defaultValue = "0") int page) {
        logger.info("Get class students request received");
        return teacherService.getClassStudentsPage(classId, page);
    }

    /**
     * 教师获取班级某个学生的信息
     *
     * @param id        教师id
     * @param studentId 学生id
     */
    @GetMapping("/teacher/classes/{classId}/students/{studentId}")
    @PreAuthorize("#id == authentication.principal.username")
    public StudentInfo getStudent(@PathVariable String id, @PathVariable String studentId) {
        logger.info("Get student request received");
        return teacherService.getStudentInfo(studentId);
    }

    /**
     * 教师获取班级发布的签到记录
     *
     * @param id      教师id
     * @param classId 班级id
     * @return 签到记录列表分页
     */
    @GetMapping("/teacher/classes/{classId}/meta")
    @PreAuthorize("#id == authentication.principal.username")
    public Page<AttendanceMeta> getMetaList(@PathVariable String id, @PathVariable String classId,
                                            @RequestParam(defaultValue = "0") int page) {
        logger.info("Get meta list request received");
        return teacherService.getMetasByClassIdPage(classId, page);
    }

    /**
     * 教师获取班级签到记录的详情
     *
     * @param id     教师id
     * @param metaId 签到记录id
     */
    @GetMapping("/teacher/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public AttendanceMeta getAttendanceMeta(@PathVariable String id, @PathVariable String metaId) {
        logger.info("Get meta request received");
        return teacherService.getMetaByMetaId(metaId);
    }

    /**
     * 客户端申请一个一个签到记录的二维码字符串，如果签到符合条件（存在且正在进行），则生成并返回
     *
     * @param id     教师id
     * @param metaId 签到记录id
     */
    @GetMapping("/teacher/meta/{metaId}/qr")
    @PreAuthorize("#id == authentication.principal.username")
    public String getAttendanceQr(@PathVariable String id, @PathVariable String metaId) {
        logger.info("Get attendance QR request received");
        return teacherService.getAttendanceQR(metaId);
    }

    /**
     * 教师获取班级学生签到情况
     *
     * @param id      教师id
     * @param classId 班级id
     * @param metaId  签到记录id
     */
    @GetMapping("/teacher/classes/{classId}/meta/{metaId}/list")  // 列出谁没签到，谁签到了
    @PreAuthorize("#id == authentication.principal.username")
    public List<List<StudentInfo>> getAttendanceCircumstance(@PathVariable String id, @PathVariable String classId,
                                                             @PathVariable String metaId) {
        logger.info("Get attendance circumstance request received");
        return teacherService.getAttendanceCircumstance(classId, metaId);
    }

    /**
     * 教师获取班级签到记录的Excel文档
     *
     * @param id      教师id
     * @param classId 班级id
     */
    @GetMapping("/teacher/classes/{classId}/getExcel")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Resource> getAttendanceExcel(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get attendance excel request received");
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
     * 教师注册
     *
     * @param id      教师id
     * @param teacher 教师信息
     */
    @PostMapping("/teacher/register")
    @PreAuthorize("#id == authentication.principal.username")
    public Teacher addTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
        logger.info("Add teacher request received");
        return teacherService.addTeacher(teacher);
    }

    /**
     * 更新教师信息
     *
     * @param teacher 教师信息
     */
    @PutMapping("/teacher")
    @PreAuthorize("#id == authentication.principal.username")
    public Teacher updateTeacher(@PathVariable String id, @RequestBody Teacher teacher) {
        logger.info("Update teacher request received");
        return teacherService.updateTeacher(teacher);
    }

    /**
     * 上传头像
     *
     * @param id   教师id
     * @param file 头像文件
     */
    @PostMapping("/teacher/uploadAvatar")
    @PreAuthorize("#id == authentication.principal.username")
    public String uploadAvatar(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        logger.info("Upload avatar request received");
        try {
            teacherService.saveAvatar(id, file.getBytes());
        } catch (Exception e) {
            return "redirect:/uploadFailure";

        }
        return "redirect:/uploadSuccess";
    }

    /**
     * 发布签到
     *
     * @param id   教师id
     * @param meta 签到记录
     * @return 签到记录
     */
    @PostMapping("/teacher/classes/{classId}/meta")
    @PreAuthorize("#id == authentication.principal.username")
    public AttendanceMeta announce(@PathVariable String id, @PathVariable String classId,
                                   @RequestBody MetaDto meta) {
        logger.info("Announce request received");
        return teacherService.announce(classId, meta);
    }

    /**
     * 更新班级
     *
     * @param id     教师id
     * @param aclass 班级信息
     */
    @PutMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public Aclass updateClass(@PathVariable String id, @RequestBody Aclass aclass) {
        logger.info("Update class request received");
        return teacherService.updateClass(aclass);
    }

    /**
     * 更新签到
     *
     * @param id   教师id
     * @param meta 签到记录
     */
    @PutMapping("/teacher/classes/{classId}/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public AttendanceMeta updateAttendanceMeta(@PathVariable String id, @RequestBody MetaDto meta, @PathVariable String metaId) {
        logger.info("Update meta request received");
        return teacherService.updateAttendanceMeta(metaId, meta);
    }

    /**
     * 删除教师
     *
     * @param id 教师id
     */
    @DeleteMapping("/teacher")
    @PreAuthorize("#id == authentication.principal.username")
    public boolean deleteTeacher(@PathVariable String id) {
        logger.info("Delete teacher request received");
        teacherService.deleteTeacher(id);
        return teacherService.getTeacherById(id) == null;
    }

    /**
     * 删除班级
     *
     * @param id      教师id
     * @param classId 班级id
     */
    @DeleteMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public boolean deleteClass(@PathVariable String id, @PathVariable String classId) {
        logger.info("Delete class request received");
        teacherService.deleteClass(classId);
        return teacherService.getClassById(classId) == null;
    }

    /**
     * 删除签到
     *
     * @param id     教师id
     * @param metaId 签到记录id
     */
    @DeleteMapping("/teacher/classes/{classId}/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public boolean deleteAttendanceMeta(@PathVariable String id, @PathVariable String metaId) {
        logger.info("Delete meta request received");
        teacherService.deleteAttendanceMeta(metaId);
        return teacherService.getMetaByMetaId(metaId) == null;
    }

    /**
     * 添加班级
     *
     * @param id    教师id
     * @param clazz 班级信息
     */
    @PostMapping("/teacher/classes")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, String>> addClassStudent(@PathVariable String id, @RequestBody AclassDto clazz) {
        logger.info("Add class request received");
        Aclass result = teacherService.addClass(id, clazz); // 为了得到新分配的班级id
        if (teacherService.getClassById(result.getId()) != null){
            Map<String, String> map = new HashMap<>();
            map.put("classId", result.getId());
            return ResponseEntity.ok(map);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 导入Excel文件添加班级学生
     *
     * @param id      教师id
     * @param classId 班级id
     * @param file    Excel文件
     * @return 对应班级导入情况 [[有效StudentInfo],[无效StudentId],[重复StudentInfo]]
     */
    @PostMapping("/teacher/classes/{classId}/postExcel")
    @PreAuthorize("#id == authentication.principal.username")
    public List<Object> addClassStudentByExcel(@PathVariable String id, @PathVariable String classId, @RequestParam(
            "file") MultipartFile file) {
        logger.info("Add class student by excel request received");
        if (!teacherService.getClassByTeacherId(id).contains(teacherService.getClassById(classId))) {
            return null;
        }
        try {
            return teacherService.addClassStudentByExcel(classId, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 删除班级学生
     *
     * @param id        教师id
     * @param classId   班级id
     * @param studentId 学生id
     */
    @DeleteMapping("/teacher/classes/{classId}/student/{studentId}")
    @PreAuthorize("#id == authentication.principal.username")
    public boolean deleteClassStudent(@PathVariable String id, @PathVariable String classId,
                                      @PathVariable String studentId) {
        logger.info("Delete class student request received");
        teacherService.deleteClassStudent(classId, studentId);
        return !teacherService.isStudentInClass(classId, studentId);
    }

    /**
     * 教师获取消息
     *
     * @param id 教师id
     */
    @GetMapping("/teacher/notification/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public AttendanceMeta getNotification(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get notification request received");
        return teacherService.getNotification(classId);
    }


    // The following are student APIs

    /**
     * 获取学生基本信息
     *
     * @param id 学生id
     */
    @GetMapping("/student/getProfile")
    @PreAuthorize("#id == authentication.principal.username")
    public StudentInfo getStudentProfile(@PathVariable String id) {
        logger.info("Get student profile request received");
        return studentService.getStudentInfoById(id);
    }

    /**
     * 设置学生头像
     *
     * @param id     学生id
     * @param avatar 头像文件
     */
    @PostMapping("/student/setAvatar")
    @PreAuthorize("#id == authentication.principal.username")
    public String setStudentAvatar(@PathVariable String id, @RequestParam("avatar") MultipartFile avatar) {
        logger.info("Set student avatar request received");
        try {
            studentService.saveAvatar(id, avatar.getBytes());
        } catch (Exception e) {
            return "uploadFailure";
        }
        return "uploadSuccess";
    }

    /**
     * 获取学生头像
     *
     * @param id        学生id
     * @param studentId 学生id
     */
    @GetMapping("/getStudentAvatar/{studentId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Resource> getStudentAvatar(@PathVariable String id, @PathVariable String studentId) {
        logger.info("Get student avatar request received");
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
     *
     * @param id      学生id
     * @param classId 班级id
     */
    @GetMapping("/student/classes/{classId}/course")
    @PreAuthorize("#id == authentication.principal.username")
    public Course getStudentClassCourse(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get student class course request received");
        return studentService.getClassCourse(classId);
    }

    /**
     * 获取某个学生的所有班级
     *
     * @param id 学生id
     */
    @GetMapping("/student/classes")
    @PreAuthorize("#id == authentication.principal.username")
    public List<Aclass> getStudentClasses(@PathVariable String id) {
        logger.info("Get student classes request received");
        return studentService.getClassByStudentId(id);
    }

    /**
     * 获取某个班级的所有学生
     *
     * @param classId 班级id
     * @param page    页码
     * @return 学生列表page
     */
    @GetMapping("/student/classes/{classId}/students")
    @PreAuthorize("#id == authentication.principal.username")
//    public List<StudentInfo> getStudentClassStudents(@PathVariable String id, @PathVariable String classId) {
//        logger.info();("Get student class students request received");
//        return studentService.getClassStudents(classId);
//    }
    public Page<StudentInfo> getStudentClassStudents(@PathVariable String id, @PathVariable String classId,
                                                     @RequestParam(value = "page", defaultValue = "0") int page) {
        logger.info("Get student class students request received");
        return studentService.getClassStudentsPage(classId, page);
    }

    /**
     * 获取某个班级的信息
     *
     * @param classId 班级id
     */
    @GetMapping("/student/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public Aclass getStudentClass(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get student class request received");
        return studentService.getClassById(classId);
    }

    /**
     * 获取某个班级的老师
     *
     * @param classId 班级id
     */
    @GetMapping("/student/classes/{classId}/teacher")
    @PreAuthorize("#id == authentication.principal.username")
    public TeacherInfo getStudentClassTeacher(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get student class teacher request received");
        return studentService.getTeacherByClassId(classId);
    }

    /**
     * 获取某个班级的所有签到
     *
     * @param classId 班级id
     * @param page 页码
     * @return 签到列表page
     */
    @GetMapping("/student/classes/{classId}/meta")
    @PreAuthorize("#id == authentication.principal.username")
    public Page<AttendanceMeta> getStudentAttendanceMeta(@PathVariable String id, @PathVariable String classId,
                                                         @RequestParam(value = "page", defaultValue = "0") int page) {
        logger.info("Get student class attendance meta request received");
        return studentService.getMetasByClassIdPage(classId, page);
    }

    /**
     * 获取某个学生在某个班级的某次签到的记录
     *
     * @param id     学生id
     * @param metaId 签到id
     */
    @GetMapping("/student/classes/{classId}/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public Attendance getStudentClassMetaRecord(@PathVariable String id, @PathVariable String metaId) {
        logger.info("Get student class meta record request received");
        return studentService.getAttendanceByStudentAndMeta(id, metaId);
    }

    /**
     * 签到码签到，插入Status为1的记录到Attendance表中
     *
     * @param id         学生id
     * @param attendance 签到信息
     */
    @PostMapping("/student/checkin1")
    @PreAuthorize("#id == authentication.principal.username")
    public boolean checkin1(@PathVariable String id, @RequestBody Attendance attendance) {
        logger.info("Student checkin1 request received");
        String classId = attendance.getAclass().getId();
        return studentService.doCheckin(id, classId, 1);
    }

    /**
     * 地理位置签到，将检查地理位置是否满足要求，满足则更新Attendance表中的Status为2和Location
     *
     * @param id         学生id
     * @param attendance 签到信息
     */
    @PutMapping("/student/checkin2")
    @PreAuthorize("#id == authentication.principal.username")
    public boolean checkin2(@PathVariable String id, @RequestBody Attendance attendance) {
        logger.info("Student checkin2 request received");
        String classId = attendance.getAclass().getId();
        Long Latitude = attendance.getLatitude();
        Long Longitude = attendance.getLongitude();
        return studentService.doLocation(id, classId, Latitude, Longitude);
    }

    /**
     * 二维码签到
     *
     * @param id         学生id
     * @param attendance 签到信息
     * @param QRCode     二维码
     */
    @PutMapping("/student/checkin3")
    @PreAuthorize("#id == authentication.principal.username")
    public boolean checkin3(@PathVariable String id, @RequestBody Attendance attendance, @RequestParam String QRCode) {
        logger.info("Student checkin3 request received");
        String classId = attendance.getAclass().getId();
        return studentService.doQR(id, classId, QRCode);
    }

}