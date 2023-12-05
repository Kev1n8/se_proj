package com.codeisright.attendance.service.impl;

import com.codeisright.attendance.data.Aclass;
import com.codeisright.attendance.data.Attendance;
import com.codeisright.attendance.data.AttendanceMeta;
import com.codeisright.attendance.data.Student;
import com.codeisright.attendance.exception.EntityNotFoundException;
import com.codeisright.attendance.repository.*;
import com.codeisright.attendance.service.CheckinService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CheckinServiceImpl implements CheckinService {
    private static final Logger logger = LoggerFactory.getLogger(CheckinService.class);
    private final AclassRepository aClassRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMetaRepository attendanceMetaRepository;

    @Autowired
    public CheckinServiceImpl(AclassRepository aClassRepository, StudentRepository studentRepository,
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
    public List<Attendance> getCheckin(String studentId, String classId) {
        try {
            return attendanceRepository.findByAclass_IdAndStudent_Id(studentId, classId);
        } catch (Exception e) {
            logger.error("Error getting checkin for student: {} in class: {}", studentId, classId, e);
            return null;
        }
    }

    @Override
    public Attendance addCheckin(String studentId, String classId, int status, LocalDateTime time, Long latitude,
                                 Long longitude) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new EntityNotFoundException(
                "Student not found with ID: " + studentId));
        Aclass aClass = aClassRepository.findById(classId).orElseThrow(() -> new EntityNotFoundException("Class not " +
                "found with ID: " + classId));
        Attendance toAdd = new Attendance(student, aClass, status, time, latitude, longitude);
        return attendanceRepository.save(toAdd);
    }

    @Override
    public Attendance forwardCheckin(String studentId, String classId, LocalDateTime time) {
        List<Attendance> records = attendanceRepository.findByAclass_IdAndStudent_Id(studentId, classId);
        Attendance recordToForward = records.stream().filter(record -> record.getTime().isBefore(time)).findFirst()
                .orElse(null);
        if (recordToForward == null) {
            logger.error("Error forwarding checkin for student: {} in class: {}", studentId, classId);
            return null;
        }
        recordToForward.setTime(time);
        recordToForward.forward();
        return attendanceRepository.save(recordToForward);
    }

    @Override
    public boolean deleteStudentClassIdCheckin(String studentId, String classId) {
        attendanceRepository.deleteById(attendanceRepository.findByAclass_IdAndStudent_Id(studentId, classId).get(0)
                .getId());
        if (attendanceRepository.findByAclass_IdAndStudent_Id(studentId, classId).size() != 0) {
            logger.error("Error deleting checkin for student: {} in class: {}", studentId, classId);
            return false;
        }
        return true;
    }

    @Override
    public List<Attendance> getCheckinByClass(String classId, LocalDateTime startTime, LocalDateTime endTime) {
        return attendanceRepository.findByAclass_IdAndTimeBetween(classId, startTime, endTime);
    }

    @Override
    public List<Attendance> getCheckinByStudent(String studentId, LocalDateTime startTime, LocalDateTime endTime) {
        return attendanceRepository.findByStudent_IdAndTimeBetween(studentId, startTime, endTime);
    }

    @Override
    public boolean canCheckin(String studentId, String classId, LocalDateTime time) {
        /*
        在这里只检查时间，QR，code和地点以及课程是否匹配的检查在controller里面做，
         */
        AttendanceMeta latest_record = attendanceMetaRepository.findFirstByIdAndIdOrderByStartDesc
                (studentId, classId);
        if (latest_record == null) {
            return false;
        }
        LocalDateTime latest_time = latest_record.getDeadline();
        return latest_time.isAfter(time);
    }

    public void doCheckin(String studentId, String classId, int status, Long latitude, Long longitude) {
        LocalDateTime time = LocalDateTime.now();
        if (!canCheckin(studentId, classId, time)) {
            throw new RuntimeException("Cannot checkin. Teacher has not started the class yet or has ended the " +
                    "class.");
        }
        addCheckin(studentId, classId, status, time, latitude, longitude);
        logger.info("Student: {} checked in for class: {}", studentId, classId);
    }

    public void doForward(String studentId, String classId) {
        LocalDateTime time = LocalDateTime.now();
        if (!canCheckin(studentId, classId, LocalDateTime.now())) {
            throw new RuntimeException("Cannot forward. Teacher has not started the class yet or has ended the " +
                    "class.");
        }
        forwardCheckin(studentId, classId, time);
        logger.info("Student: {} forwarded checkin for class: {}", studentId, classId);
    }
}