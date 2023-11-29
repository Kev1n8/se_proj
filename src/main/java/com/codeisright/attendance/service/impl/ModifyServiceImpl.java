package com.codeisright.attendance.service.impl;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.exception.EntityNotFoundException;
import com.codeisright.attendance.repository.*;
import com.codeisright.attendance.service.ModifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(rollbackFor = Exception.class)
public class ModifyServiceImpl implements ModifyService {
    private static final Logger logger = LoggerFactory.getLogger(ModifyServiceImpl.class);
    private final AClassRepository aClassRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMetaRepository attendanceMetaRepository;

    @Autowired
    private ModifyServiceImpl(AClassRepository aClassRepository, StudentRepository studentRepository,
                              TeacherRepository teacherRepository, CourseRepository courseRepository,
                              AttendanceRepository attendanceRepository,
                              AttendanceMetaRepository attendanceMetaRepository) {
        this.aClassRepository = aClassRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.courseRepository = courseRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceMetaRepository = attendanceMetaRepository;
    }

    @Override
    public boolean addClass(String title, String description, int grade, String courseId, String teacherId) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new EntityNotFoundException("Course not" +
                " found with ID: " + courseId));
        Teacher teacher = teacherRepository.findById(teacherId).orElseThrow(() -> new EntityNotFoundException(
                "Teacher not found with ID: " + teacherId));
        String classId = aClassRepository.save(new AClass(title, description, grade, course, teacher)).getId();
        if (classId == null) {
            logger.error("Error adding class with title: {}", title);
            return false;
        }
        logger.info("Class added successfully with ID: {}, title: {}", classId, title);
        return true;
    }

    @Override
    public boolean addCourse(String courseCode, String courseName) {
        Course toAdd = new Course(courseName, courseCode);
        String courseId = courseRepository.save(toAdd).getId();
        if (courseId == null) {
            logger.error("Error adding course with name: {}", courseName);
            return false;
        }
        logger.info("Course added successfully with ID: {}, name: {}", courseId, courseName);
        return true;
    }

    @Override
    public boolean addTeacher(String teacherId, String teacherName, int age, String gender, String department) {
        String id = teacherRepository.save(new Teacher(teacherId, teacherName, age, gender, department)).getId();
        if (id == null) {
            logger.error("Error adding teacher with name: {}", teacherName);
            return false;
        }
        logger.info("Teacher added successfully with ID: {}, name: {}", teacherId, teacherName);
        return true;
    }

    @Override
    public boolean announceAttendance(int requirement, LocalDateTime startTime, LocalDateTime deadline,
                                      String classId) {
        AClass aClass = aClassRepository.findById(classId).orElseThrow(() -> new EntityNotFoundException("Class " +
                "not found with ID: " + classId));
        String id = attendanceMetaRepository.save(new AttendanceMeta(requirement, startTime, deadline, aClass)).getId();
        if (id == null) {
            logger.error("Error announcing attendance with requirement: {}", requirement);
            return false;
        }
        logger.info("Attendance announced successfully with requirement: {}", requirement);
        return true;
    }

    @Override
    public boolean deleteClass(String classId) {
        AClass toDelete = aClassRepository.findById(classId).orElseThrow(() -> new EntityNotFoundException("Class" +
                " not found with ID: " + classId));
        aClassRepository.delete(toDelete);
        AClass result = aClassRepository.findById(classId).orElse(null);
        if (result != null) {
            logger.error("Error deleting class with ID: {}", classId);
            return false;
        }
        logger.info("Class deleted successfully with ID: {}", classId);
        return true;
    }

    @Override
    public boolean deleteCourse(String courseName) {
        Course toDelete = courseRepository.findByName(courseName);
        courseRepository.delete(toDelete);
        Course result = courseRepository.findByName(courseName);
        if (result != null) {
            logger.error("Error deleting course with name: {}", courseName);
            return false;
        }
        logger.info("Course deleted successfully with name: {}", courseName);
        return true;
    }

    @Override
    public boolean deleteTeacher(String teacherId) {
        Teacher toDelete = teacherRepository.findById(teacherId).orElseThrow(() -> new EntityNotFoundException(
                "Teacher not found with ID: " + teacherId));
        teacherRepository.delete(toDelete);
        Teacher result = teacherRepository.findById(teacherId).orElse(null);
        if (result != null) {
            logger.error("Error deleting teacher with ID: {}", teacherId);
            return false;
        }
        logger.info("Teacher deleted successfully with ID: {}", teacherId);
        return true;
    }

    @Override
    public boolean deleteAnnouncement(String metaId) {
        AttendanceMeta toDelete =
                attendanceMetaRepository.findById(metaId).orElseThrow(() -> new EntityNotFoundException(
                        "AttendanceMeta not found with ID: " + metaId));
        attendanceMetaRepository.delete(toDelete);
        AttendanceMeta result = attendanceMetaRepository.findById(metaId).orElse(null);
        if (result != null) {
            logger.error("Error deleting AttendanceMeta with ID: {}", metaId);
            return false;
        }
        logger.info("AttendanceMeta deleted successfully with ID: {}", metaId);
        return true;
    }

    @Override
    public boolean modifyClass(String classId, String title, int grade) {
        AClass toModify = aClassRepository.findById(classId).orElseThrow(() -> new EntityNotFoundException("Class" +
                " not found with ID: " + classId));
        String originalTitle = toModify.getTitle();
        int originalGrade = toModify.getGrade();
        toModify.setTitle(title);
        toModify.setGrade(grade);
        if (toModify.getTitle().equals(originalTitle) && toModify.getGrade() == originalGrade) {
            logger.error("Error modifying class with ID: {}", classId);
            return false;
        }
        aClassRepository.save(toModify);
        logger.info("Class modified successfully with ID: {}", classId);
        return true;
    }

    @Override
    public boolean modifyCourse(String courseName, String courseCode) {
        Course toModify = courseRepository.findByName(courseName);
        String originalCode = toModify.getCourseCode();
        toModify.setCourseCode(courseCode);
        courseRepository.save(toModify);
        if (toModify.getCourseCode().equals(originalCode)) {
            logger.error("Error modifying course code of name: {}", courseName);
            return false;
        }
        logger.info("Course modified successfully with name: {}", courseName);
        return true;
    }

    @Override
    public boolean modifyTeacher(String teacherId, String name, String department) {
        Teacher toModify = teacherRepository.findById(teacherId).orElseThrow(() -> new EntityNotFoundException(
                "Teacher not found with ID: " + teacherId));
        String originalName = toModify.getName();
        String originalDepartment = toModify.getDepartment();
        toModify.setName(name);
        toModify.setDepartment(department);
        teacherRepository.save(toModify);
        if (toModify.getName().equals(originalName) && toModify.getDepartment().equals(originalDepartment)) {
            logger.error("Error modifying teacher with ID: {}", teacherId);
            return false;
        }
        logger.info("Teacher modified successfully with ID: {}", teacherId);
        return true;
    }

    @Override
    public boolean modifyAnnouncement(String id, LocalDateTime deadline) {
        AttendanceMeta toModify =
                attendanceMetaRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(
                        "AttendanceMeta not found with ID: " + id));
        LocalDateTime originalDeadline = toModify.getDeadline();
        toModify.setDeadline(deadline);
        attendanceMetaRepository.save(toModify);
        if (toModify.getDeadline().equals(originalDeadline)) {
            logger.error("Error modifying AttendanceMeta with ID: {}", id);
            return false;
        }
        logger.info("AttendanceMeta modified successfully with ID: {}, new ddl: {}", id, deadline);
        return true;
    }
}