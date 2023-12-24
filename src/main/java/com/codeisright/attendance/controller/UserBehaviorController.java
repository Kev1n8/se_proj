/**
 * Project Name: Online Attendance Management System
 * File Name: UserBehaviorController.java
 * Author: Kaifeng Zheng
 */

package com.codeisright.attendance.controller;

import com.codeisright.attendance.dto.*;
import com.codeisright.attendance.data.*;
import com.codeisright.attendance.service.StudentService;
import com.codeisright.attendance.service.TeacherService;
import com.codeisright.attendance.view.AclassInfo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8080")
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

    private Map<String, Object> getMap(String key, Object info) {
        Map<String, Object> result = new HashMap<>();
        result.put(key, info);
        return result;
    }

    /**
     * 获取教师个人信息
     *
     * @param id 教师id
     */
    @GetMapping("/teacher/getProfile")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getTeacherProfile(@PathVariable String id) {
        logger.info("Get teacher profile request received");
        TeacherInfo result = teacherService.getTeacherInfoById(id);
        return ResponseEntity.ok(getMap("teacher", result));
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
    public ResponseEntity<Map<String, Object>> getTeacherClasses(@PathVariable String id) {
        logger.info("Get teacher classes request received");
        List<AclassInfo> result = teacherService.getClassInfoByTeacherId(id);
        if (result == null) {
            logger.info("not found teacher");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "还未创建任何班级"));
        }
        return ResponseEntity.ok(getMap("classes", result));
    }

    /**
     * 教师获取班级信息详情
     *
     * @param classId 班级id
     */
    @GetMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getTeacherClass(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get teacher class request received");
        AclassInfo toReturn = teacherService.getClassInfo(classId);
        if (toReturn == null) {
            logger.info("Class not found");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(getMap("message", "不存在的班级"));
        }
        return ResponseEntity.ok(getMap("class", toReturn));
    }

    /**
     * 教师获取班级所属课程
     *
     * @param classId 班级id
     */
    @GetMapping("/teacher/classes/{classId}/course")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getClassCourse(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get class course request received");
        Course toReturn = teacherService.getClassCourse(classId);
        if (toReturn == null) {
            logger.info("Course not found");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(getMap("message", "不存在的课程"));
        }
        return ResponseEntity.ok(getMap("course", toReturn));
    }

    /**
     * 教师获取班级学生列表
     *
     * @param classId 班级id
     * @return 学生列表
     */
    @GetMapping("/teacher/classes/{classId}/students")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getTeacherClassStudents(@PathVariable String id, @PathVariable String classId,
                                                     @RequestParam(defaultValue = "0") int page) {
        logger.info("Get class students request received");
        if (teacherService.getClassInfo(classId) == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message","不存在的班级"));
        }
        Page<StudentInfo> result = teacherService.getClassStudentsPage(classId, page);
        return ResponseEntity.ok(getMap("Page",result));
    }

    /**
     * 教师获取某个学生的信息
     *
     * @param id        教师id
     * @param studentId 学生id
     */
    @GetMapping("/teacher/students/{studentId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getStudent(@PathVariable String id, @PathVariable String studentId) {
        logger.info("Get student request received");
        StudentInfo toReturn = teacherService.getStudentInfo(studentId);
        if (toReturn == null) {
            logger.info("Student not found");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(getMap("message", "找不到该学生"));
        }
        return ResponseEntity.ok(getMap("student", toReturn));
    }

    /**
     * 教师获取班级发布的签到记录
     *
     * @param id      教师id
     * @param classId 班级id
     * @return 签到记录列表分页，如果班级不存在会返回404
     */
    @GetMapping("/teacher/classes/{classId}/meta")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getMetaList(@PathVariable String id, @PathVariable String classId,
                                                           @RequestParam(defaultValue = "0") int page) {
        logger.info("Get meta list request received");
        if (teacherService.getClassInfo(classId) == null) {
            logger.info("Class not found");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(getMap("message", "找不到该班级"));
        }
        return ResponseEntity.ok(getMap("Page", MetaDto.Convert(teacherService.getMetasByClassIdPage(classId, page))));
    }

    /**
     * 教师获取班级签到记录的详情
     *
     * @param id     教师id
     * @param metaId 签到记录id
     */
    @GetMapping("/teacher/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getAttendanceMeta(@PathVariable String id, @PathVariable String metaId) {
        logger.info("Get meta request received");
        AttendanceMeta result = teacherService.getMetaByMetaId(metaId);
        if (result == null) {
            logger.info("Meta not found");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(getMap("message", "未找到该签到发布记录"));
        }
        return ResponseEntity.ok(getMap("meta", new MetaDto(result)));
    }

    /**
     * 客户端申请一个一个签到记录的二维码字符串，如果签到符合条件（存在且正在进行），则生成并返回
     *
     * @param id     教师id
     * @param metaId 签到记录id
     * @return "qr" if success, "not found" if not found, "not available" if not available
     */
    @GetMapping("/teacher/meta/{metaId}/qr")
    @PreAuthorize("#id == authentication.principal.username")
    public String getAttendanceQr(@PathVariable String id, @PathVariable String metaId) {
        logger.info("Get attendance QR request received");
        return teacherService.getAttendanceQR(metaId);
    }

    private List<Map<String, String>> getStuInfoMapList(List<StudentInfo> list) {
        List<Map<String, String>> sb = new ArrayList<>();
        for (StudentInfo studentInfo : list) {
            HashMap<String, String> map = new HashMap<>();
            map.put("studentId", studentInfo.getId());
            map.put("studentName", studentInfo.getName());
            map.put("age", studentInfo.getAge() + "");
            map.put("gender", studentInfo.getGender());
            map.put("major", studentInfo.getMajor());
            map.put("description", studentInfo.getDescription());
            sb.add(map);
        }
        return sb;
    }

    /**
     * 教师获取班级学生签到情况
     *
     * @param id      教师id
     * @param metaId  签到记录id
     * @return {"present": [stu1, stu2, ...], "absent": [stu1, stu2, ...], "makeup": [stu1, stu2, ...]"}
     */
    @GetMapping("/teacher/meta/{metaId}/list")  // 列出谁没签到，谁签到了
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getAttendanceCircumstance(@PathVariable String id,
                                                                                            @PathVariable String metaId) {
        logger.info("Get attendance circumstance request received");
        List<List<StudentInfo>> circumstance = teacherService.getAttendanceCircumstance(metaId);

        if (circumstance == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, List<Map<String, String>>> toReturn = new HashMap<>();
        List<StudentInfo> present = circumstance.get(0);
        List<StudentInfo> absent = circumstance.get(1);
        List<StudentInfo> makeup = circumstance.get(2);

        List<Map<String, String>> absentMap = getStuInfoMapList(absent);
        List<Map<String, String>> presentMap = getStuInfoMapList(present);
        toReturn.put("absent", absentMap);
        toReturn.put("present", presentMap);
        toReturn.put("makeup", getStuInfoMapList(makeup));

        return ResponseEntity.ok(toReturn);
    }

    /**
     * 老师实时获取签到人数
     *
     * @param id 老师id
     * @param metaId 签到id
     * @return 比率
     */
    @GetMapping("/teacher/meta/{metaId}/getRealTime")
    public ResponseEntity<Map<String, Object>> getRateRealTime(@PathVariable String id,
                                                               @PathVariable String metaId){
        if (teacherService.getMetaByMetaId(metaId)==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "不存在的签到记录"));
        }
        List<List<StudentInfo>> circumstance = teacherService.getAttendanceCircumstance(metaId);
        int present = circumstance.get(0).size();
        int all = circumstance.get(0).size() + circumstance.get(1).size() + circumstance.get(2).size();
        String toShown = String.format("%d/%d", present, all);
        return ResponseEntity.ok(getMap("Rate", toShown));
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
        String title = teacherService.getClassInfo(classId).getTitle();
        ByteArrayResource resource = new ByteArrayResource(excelBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(resource.contentLength());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", title + ".xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    /**
     * 更新教师信息，以url中的id为准，id不可以修改
     *
     * @param teacherDto 教师信息
     */
    @PutMapping("/teacher")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> updateTeacher(@PathVariable String id,
                                                             @RequestBody TeacherDto teacherDto) {
        logger.info("Update teacher request received");
        if (teacherService.getTeacherById(id) == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "不存在的老师"));
        }
        TeacherInfo result = teacherService.updateTeacher(id, teacherDto).toTeacherInfo();
        return ResponseEntity.ok(getMap("teacher", result));
    }

    /**
     * 教师更改密码
     *
     * @param id          教师id
     * @param userDto 新密码存在里面
     * @return Teacher 对象
     */
    @PutMapping("/teacher/password")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> updateTeacherPassword(@PathVariable String id,
                                                              @RequestBody UserDto userDto) {
        logger.info("Update password request received");
        String newPassword = userDto.getPassword();
        Teacher result = teacherService.updatePassword(id, newPassword);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "不存在的老师"));
        }
        return ResponseEntity.ok(getMap("teacher", result.toTeacherInfo()));
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
     * 发布签到，发布的Body中不需要包含id，由后端生成
     *
     * @param id   教师id
     * @param meta 签到记录
     * @return 签到记录
     */
    @PostMapping("/teacher/classes/{classId}/meta")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> announce(@PathVariable String id, @PathVariable String classId,
                            @RequestBody MetaDto meta) {
        logger.info("Announce request received");
        AttendanceMeta result = teacherService.announce(classId, meta);
        if (result == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "不存在的班级"));
        }
        return ResponseEntity.ok(getMap("meta", new MetaDto(result)));
    }

    /**
     * 更新班级
     *
     * @param id     教师id
     * @param aclass 班级信息
     */
    @PutMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> updateClass(@PathVariable String id, @PathVariable String classId, @RequestBody AclassDto aclass) {
        logger.info("Update class request received");
        Aclass result = teacherService.updateClass(classId, aclass);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "不存在的班级、老师或课程"));
        }
        return ResponseEntity.ok(getMap("class", result.toAclassInfo()));
    }

    /**
     * 更新签到，以url中的metaId为主，不会考虑meta中的id，避免id被篡改
     *
     * @param id   教师id
     * @param meta 签到记录
     */
    @PutMapping("/teacher/classes/{classId}/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> updateAttendanceMeta(@PathVariable String id, @RequestBody MetaDto meta,
                                        @PathVariable String metaId) {
        logger.info("Update meta request received");
        AttendanceMeta result = teacherService.updateAttendanceMeta(metaId, meta);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "不存在的签到记录"));
        }
        return ResponseEntity.ok(getMap("meta", new MetaDto(result)));
    }

    /**
     * 删除教师
     *
     * @param id 教师id
     */
    @DeleteMapping("/teacher")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, String>> deleteTeacher(@PathVariable String id) {
        logger.info("Delete teacher request received");
        teacherService.deleteTeacher(id);
        if (teacherService.getTeacherById(id) == null) {
            Map<String, String> map = new HashMap<>();
            map.put("status", "success");
            return ResponseEntity.ok(map);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("status", "fail");
            return ResponseEntity.ok(map);
        }
    }

    /**
     * 删除班级
     *
     * @param id      教师id
     * @param classId 班级id
     */
    @DeleteMapping("/teacher/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, String>> deleteClass(@PathVariable String id, @PathVariable String classId) {
        logger.info("Delete class request received");
        teacherService.deleteClass(classId);
        if (teacherService.getClassById(classId) == null) {
            Map<String, String> map = new HashMap<>();
            map.put("status", "success");
            return ResponseEntity.ok(map);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("status", "fail");
            return ResponseEntity.ok(map);
        }
    }

    /**
     * 删除签到
     *
     * @param id     教师id
     * @param metaId 签到记录id
     */
    @DeleteMapping("/teacher/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, String>> deleteAttendanceMeta(@PathVariable String id,
                                                                    @PathVariable String metaId) {
        logger.info("Delete meta request received");
        teacherService.deleteAttendanceMeta(metaId);
        if (teacherService.getMetaByMetaId(metaId) == null) {
            Map<String, String> map = new HashMap<>();
            map.put("status", "success");
            return ResponseEntity.ok(map);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("status", "fail");
            return ResponseEntity.ok(map);
        }
    }

    /**
     * 添加班级
     *
     * @param id    教师id
     * @param clazz 班级信息
     */
    @PostMapping("/teacher/classes")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> addClassStudent(@PathVariable String id, @RequestBody AclassDto clazz) {
        logger.info("Add class request received");
        Aclass result = teacherService.addClass(id, clazz); // 为了得到新分配的班级id
        if (result.getId() == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "不存在的老师或课程"));
        }
        return ResponseEntity.ok(getMap("class", result.toAclassInfo()));
    }

    /**
     * 导入Excel文件添加班级学生
     * 注意，老师每次导入都会清空之前该班级的所有学生和发布过的签到
     *
     * @param id      教师id
     * @param classId 班级id
     * @param file    Excel文件
     * @return 对应班级导入情况 [[有效StudentInfo],[无效StudentId],[重复StudentInfo]]
     */
    @PostMapping("/teacher/classes/{classId}/postExcel")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> addClassStudentByExcel(@PathVariable String id,
                                                                      @PathVariable String classId,
                                                                      @RequestParam("file") MultipartFile file) {
        logger.info("Add class student by excel request received");
        if (!teacherService.getClassByTeacherId(id).contains(teacherService.getClassById(classId))) {  // 判断是否为该教师的班级
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            List<Object> res = teacherService.addClassStudentByExcel(classId, file.getBytes());
            Map<String, Object> map = new HashMap<>();
            map.put("valid", res.get(0));
            map.put("invalid", res.get(1));
            map.put("duplicate", res.get(2));
            return ResponseEntity.ok(map);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
    public ResponseEntity<Map<String, String>> deleteClassStudent(@PathVariable String id, @PathVariable String classId,
                                                                  @PathVariable String studentId) {
        logger.info("Delete class student request received");
        teacherService.deleteClassStudent(classId, studentId);
        if (!teacherService.isStudentInClass(classId, studentId)) {
            Map<String, String> map = new HashMap<>();
            map.put("status", "success");
            return ResponseEntity.ok(map);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put("status", "fail");
            return ResponseEntity.ok(map);
        }
    }

    /**
     * 教师获取消息，只会返回 *属于该老师* *已到期* *还没通知过* 的签到
     * 发送的逻辑是，如果签到记录符合发送条件且还没被发送过，则发送并标记已发送
     * 下次再次请求除非再有新记录符合条件否则不会发送新消息
     * 发送的是该班级所有还没有通知的签到记录
     *
     * @param id 教师id
     * @return 一个通知列表[meta]
     */
    @GetMapping("/teacher/notification")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getNotification(@PathVariable String id) {
        logger.info("Get notification request received");
        List<AttendanceMeta> result = teacherService.getNotification(id);
        if (result==null) {
            return ResponseEntity.ok(getMap("message", "没有新消息"));
        } else {
            return ResponseEntity.ok(getMap("meta", MetaDto.Convert(result)));
        }
    }

    /**
     * 给学生补签
     * 在数据库中查找meta，然后检查是否在该班级，然后才能补签，只需要提供签到DTO(只需包含学号和metaId，其他项为空串)
     *
     * @param id      教师id
     * @param attendanceDto 存放学生Id，metaId
     */
    @PostMapping("/teacher/makeup")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> supplementAttendance(@PathVariable String id,
                                                                    @RequestBody AttendanceDto attendanceDto) {
        logger.info("Supplement attendance request received");
        Attendance res = teacherService.makeUpAttendance(attendanceDto);
        if (res!=null) {
            return ResponseEntity.ok(getMap("attendance", new AttendanceDto(res)));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(getMap("message", "补签失败"));
        }
    }


    // The following are student APIs

    /**
     * 获取学生基本信息
     *
     * @param id 学生id
     */
    @GetMapping("/student/getProfile")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getStudentProfile(@PathVariable String id) {
        logger.info("Get student profile request received");
        StudentInfo result = studentService.getStudentInfoById(id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message","学生还没加入任何班级"));
        } else {
            return ResponseEntity.ok(getMap("student", result));
        }
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
    public ResponseEntity<Map<String, Object>> getStudentClassCourse(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get student class course request received");
        Course result = studentService.getClassCourse(classId);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message","找不到这个班级"));
        } else {
            return ResponseEntity.ok(getMap("course", result));
        }
    }

    /**
     * 获取某个学生的所有班级
     *
     * @param id 学生id
     */
    @GetMapping("/student/classes")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getStudentClasses(@PathVariable String id) {
        logger.info("Get student classes request received");
        List<AclassInfo> result = studentService.getClassInfoByStudentId(id);
        if (result == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message","找不到这个学生"));
        } else {
            return ResponseEntity.ok(getMap("classes", result));
        }
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
    public ResponseEntity<Map<String, Object>> getStudentClassStudents(@PathVariable String id, @PathVariable String classId,
                                                     @RequestParam(value = "page", defaultValue = "0") int page) {
        logger.info("Get student class students request received");
        if (teacherService.getClassInfo(classId) == null) {
            logger.info("Class not found");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(getMap("message", "找不到该班级"));
        }
        return ResponseEntity.ok(getMap("Page",studentService.getClassStudentsPage(classId, page)));
    }

    /**
     * 获取某个班级的信息
     *
     * @param classId 班级id
     */
    @GetMapping("/student/classes/{classId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getStudentClass(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get student class request received");
        Aclass result = studentService.getClassById(classId);
        if (result==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "不存在的班级"));
        }
        return ResponseEntity.ok(getMap("class",result.toAclassInfo()));
    }

    /**
     * 获取某个班级的老师
     *
     * @param classId 班级id
     */
    @GetMapping("/student/classes/{classId}/teacher")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getStudentClassTeacher(@PathVariable String id, @PathVariable String classId) {
        logger.info("Get student class teacher request received");
        if (teacherService.getClassInfo(classId) == null) {
            logger.info("Class not found");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(getMap("message", "找不到该班级"));
        }
        TeacherInfo result = studentService.getTeacherByClassId(classId);
        return ResponseEntity.ok(getMap("teacher",result));
    }

    /**
     * 获取某个班级的所有签到
     *
     * @param classId 班级id
     * @param page    页码
     * @return 签到列表page
     */
    @GetMapping("/student/classes/{classId}/meta")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getStudentAttendanceMeta(@PathVariable String id, @PathVariable String classId,
                                                  @RequestParam(value = "page", defaultValue = "0") int page) {
        logger.info("Get student class attendance meta request received");
        if (teacherService.getClassInfo(classId) == null) {
            logger.info("Class not found");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(getMap("message", "找不到该班级"));
        }
        Page<AttendanceMeta> result = studentService.getMetasByClassIdPage(classId, page);
        return ResponseEntity.ok(getMap("Page", MetaDto.Convert(result)));
    }

    /**
     * 获取学生在某次签到的记录
     *
     * @param id     学生id
     * @param metaId 签到id
     */
    @GetMapping("/student/meta/{metaId}")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getStudentClassMetaRecord(@PathVariable String id,
                                                                         @PathVariable String metaId) {
        logger.info("Get student class meta record request received");
        Attendance record = studentService.getAttendanceByStudentAndMeta(id, metaId);
        if (record == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "学生没有参加这次签到"));
        }
        return ResponseEntity.ok(getMap("record", new AttendanceDto(record)));
    }

    /**
     * 获取学生签到列表
     *
     * @param id 学生id
     * @param classId 班级id
     * @param page 页数
     * @return 附带学生签到情况的签到列表分页
     */
    @GetMapping("/student/classes/{classId}/list")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> getStudentClassMetaList(@PathVariable String id,
                                                                       @PathVariable String classId,
                                                                       @RequestParam(value = "page", defaultValue = "0") int page) {
        logger.info("Get student request for class meta with status");
        Page<StudentMetaRecord> res = studentService.getStudentRecords(classId, id, page);
        if (res==null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMap("message","发生未知错误"));
        }
        return ResponseEntity.ok(getMap("Page", res));
    }

    /**
     * 学生更新资料
     *
     * @param id      学生id
     * @param studentDto 学生信息
     * @return 学生信息 或 失败
     */
    @PutMapping("/student/update")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> updateStudent(@PathVariable String id, @RequestBody StudentDto studentDto){
        logger.info("Update student request received");
        Student res = studentService.updateStudent(id, studentDto);
        if (res==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "找不到该学生"));
        }
        return ResponseEntity.ok(getMap("student", res.toStudentInfo()));
    }

    /**
     * 学生修改密码
     *
     * @param id 学号
     * @param userDto 密码载体
     */
    @PutMapping("/student/password")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> updateStudentPassword(@PathVariable String id, @RequestBody UserDto userDto){
        logger.info("Update student password request received");
        Student res = studentService.updatePassword(id, userDto.getPassword());
        if (res==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "找不到该学生"));
        }
        return ResponseEntity.ok(getMap("student", res.toStudentInfo()));
    }

    /**
     * 签到码签到，插入Status为1的记录到Attendance表中
     * 第一次签到需要的信息：学生id，班级id，签到码，签到时间
     *
     * @param id         学生id
     * @param attendance 签到信息
     */
    @PostMapping("/student/checkin1")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> checkin1(@PathVariable String id,
                                                        @RequestBody AttendanceDto attendance) {
        logger.info("Student checkin1 request received");
        String metaId = attendance.getMetaId();
        int res = studentService.doCheckin(id, metaId);
        return switch (res) {
            case 0 -> ResponseEntity.ok(getMap("status", "success"));
            case 1 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "学生不在这个班级"));
            case 2 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "签到没开始或已结束"));
            case 3 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "学生已经完成签到Step1"));
            case 4 -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "没有匹配的签到码"));
            case 5 -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMap("message", "签到时发生未知错误"));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMap("message", "系统发生未知错误"));
        };
    }

    /**
     * 地理位置签到，将检查地理位置是否满足要求，满足则更新Attendance表中的Status为2和Location
     * 第二次签到需要的信息：学生id，班级id，经度，纬度，签到时间，班级id
     *
     * @param id         学生id
     * @param attendance 签到信息
     * @return 签到结果
     */
    @PutMapping("/student/checkin2")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> checkin2(@PathVariable String id,
                                                        @RequestBody AttendanceDto attendance) {
        logger.info("Student checkin2 request received");
//        String metaId = attendance.getMetaId();
        String classId = attendance.getClassId();
        Long Latitude = attendance.getLatitude();
        Long Longitude = attendance.getLongitude();
        int res = studentService.doLocation(id, classId, Latitude, Longitude);
        return switch (res){
            case 0 -> ResponseEntity.ok(getMap("status", "success"));
            case 1 -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "不存在的班级或签到"));
            case 2 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "学生还没有完成签到Step1或状态不为1"));
            case 3 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "有效期已过"));
            case 4 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "地理位置不符合要求"));
            case 5 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "已经完成签到要求，无需继续签到"));
            case 6 -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMap("message", "签到时发生未知错误"));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMap("message", "系统发生未知错误"));
        };
    }

    /**
     * 二维码签到
     * 第三次签到需要的信息：学生id，二维码字符串（会包含签到需要的信息）
     *
     * @param id         学生id
     * @param QRCode     二维码
     * @return 签到结果
     */
    @PutMapping("/student/checkin3")
    @PreAuthorize("#id == authentication.principal.username")
    public ResponseEntity<Map<String, Object>> checkin3(@PathVariable String id,
                                                        @RequestParam String QRCode) {
        logger.info("Student checkin3 request received");
        int res = studentService.doQR(id, QRCode);
        return switch (res){
            case 0 -> ResponseEntity.ok(getMap("status", "success"));
            case 1 -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(getMap("message", "没有对应签到码"));
            case 2 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "学生还没有完成签到Step2或状态不为2"));
            case 3 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "有效期已过"));
            case 4 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "二维码已过期"));
            case 5 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "未知的二维码"));
            case 6 -> ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getMap("message", "已经完成签到了"));
            case 7 -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMap("message", "签到时发生未知错误"));
            default -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getMap("message", "系统发生未知错误"));
        };
    }
}