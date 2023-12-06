package com.codeisright.attendance.service;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.repository.*;
import com.codeisright.attendance.utils.ImageUtils;
import com.codeisright.attendance.utils.QRCodeUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class StudentService extends UserService{
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(TeacherRepository teacherRepository, AclassRepository aclassRepository,
                          EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
                          AttendanceRepository attendanceRepository, AttendanceMetaRepository attendanceMetaRepository,
                          CourseRepository courseRepository) {
        super(teacherRepository, aclassRepository, enrollmentRepository, studentRepository, attendanceRepository,
                attendanceMetaRepository, courseRepository);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Get a student account by id.
     * @param id
     */
    public Student getStudentById(String id) {
        return studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Error finding student " +
                "with id: " + id));
    }

    /**
     * Update a student account in the database.
     * @param student
     */
    public Student updateStudent(Student student) {
        Student existingStudent =
                studentRepository.findById(student.getUsername()).orElseThrow(() -> new EntityNotFoundException(
                        "Error " +
                                "finding student with id: " + student.getUsername()));
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());
        existingStudent.setGender(student.getGender());
        return studentRepository.save(existingStudent);
    }

    /**
     * Delete a student account from the database.
     * @param id
     */
    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }

    /**
     * Add a new student to the database.
     * @param id
     * @param studentName
     * @param codedPassword
     * @param age
     * @param gender
     * @param major
     * @param studentClass
     */
    public Student registerStudent(String id, String studentName, String codedPassword, int age, String gender,
                                   String major, String studentClass) {
        Student toAdd = studentRepository.findById(id).orElse(null);
        if (toAdd != null) {
            return null;
        }
        toAdd = new Student(id, studentName, codedPassword, age, gender, major, studentClass);
        return toAdd;
    }

    /**
     * Login a student with id and password.
     * @param id
     * @param password
     */
    public Student loginStudent(String id, String password) {
        //TODO: security configuration
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return null;
        }
        if (student.getCodedpassword().matches(password)) {
            return student;
        }
        return null;
    }

    /**
     * Get the attendance record of a student in a single metaAtt(if checkin step1 successfully before).
     * @param studentId
     * @param attendanceMetaId
     */
    public Attendance getAttendanceByStudentAndMeta(String studentId, String attendanceMetaId) {
        return attendanceRepository.findByStudent_IdAndMeta_Id(studentId, attendanceMetaId);
    }

    /**
     * Add a new checkin record to the attendance table.
     * @param studentId
     * @param classId
     * @param status
     * @param time
     */
    public void addCheckin(String studentId, String classId, int status, LocalDateTime time) {
        Student student =
                studentRepository.findById(studentId).orElseThrow(() -> new com.codeisright.attendance.exception.EntityNotFoundException(
                "Student not found with ID: " + studentId));
        Aclass aClass =
                aclassRepository.findById(classId).orElseThrow(() -> new com.codeisright.attendance.exception.EntityNotFoundException("Class not " +
                "found with ID: " + classId));
        Attendance toAdd = new Attendance(student, aClass, status, time, null, null);
        attendanceRepository.save(toAdd);
    }

    /**
     * Forward the latest checkin record to the current time. If location value is -1, then keep the original value.
     * @param recordToForward
     * @param time          current time
     * @param latitude
     * @param longitude
     */
    public void forwardCheckin(Attendance recordToForward, LocalDateTime time, Long latitude, Long longitude) {
        if (recordToForward == null) {
            logger.error("Error forwarding checkin record:" + recordToForward);
            return;
        }
        recordToForward.setTime(time);
        recordToForward.forward();
        if(latitude!=-1&&longitude!=-1) {
            recordToForward.setLatitude(latitude);
            recordToForward.setLongitude(longitude);
        }
        attendanceRepository.save(recordToForward);
    }

    /**
     * Calculate the distance(meters) between two locations.
     * @param lat1
     * @param lon1
     * @param lat2
     * @param lon2
     */
    public double calculateDistance(Long lat1, Long lon1, Long lat2, Long lon2) {
        int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c; // convert to kilometers
        return distance;
    }

    /**
     * Check if the location is acceptable, which means the distance between the two locations should be less than 100m.
     * @param latitude
     * @param longitude
     * @param latitude2
     * @param longitude2
     */
    public boolean acceptableLocation(Long latitude, Long longitude, Long latitude2, Long longitude2) {
        double distance = calculateDistance(latitude, longitude, latitude2, longitude2);
        return distance < 0.1;
    }

    /**
     * Check if the current time is in the time range of the latest attendance meta.
     * @param classId
     * @param time
     */
    public boolean isInTime(String classId, LocalDateTime time) {
        AttendanceMeta latest_record = attendanceMetaRepository.findFirstByAclass_IdOrderByDeadlineDesc(classId);
        if (latest_record == null) {
            return false;
        }
        LocalDateTime latest_time = latest_record.getDeadline();
        return latest_time.isAfter(time);
    }

    /**
     * Checkin step1 for a student.
     * @param studentId
     * @param classId
     * @param status
     */
    public boolean doCheckin(String studentId, String classId, int status) {
        LocalDateTime time = LocalDateTime.now();
        if (!isInTime(classId, time)) {
            throw new RuntimeException("Cannot checkin. Teacher has not started the class yet or has ended the " +
                    "class.");
        }
        addCheckin(studentId, classId, status, time);
        logger.info("Student: {} checked in for class: {}", studentId, classId);
        return true;
    }

    /**
     * Checkin step2 for a student.
     * @param studentId
     * @param classId
     * @param latitute
     * @param longitute
     */
    public boolean doLocation(String studentId, String classId, Long latitute, Long longitute) {
        LocalDateTime time = LocalDateTime.now();
        AttendanceMeta latest_record = attendanceMetaRepository.findFirstByAclass_IdOrderByDeadlineDesc(classId);
        String metaId = latest_record.getId();
        Attendance original = attendanceRepository.findByStudent_IdAndMeta_Id(studentId, metaId);
        Long l1 = latest_record.getLatitude();
        Long l2 = latest_record.getLongitude();

        if (!isInTime(classId, LocalDateTime.now())) {
            throw new RuntimeException("Cannot forward. Teacher has not started the class yet or has ended the " +
                    "class.");
        }
        if (original==null){//没有先进行签到码签到
            throw new RuntimeException("Cannot forward. Student has not checked in yet.");
        }
        if(original.getStatus()!=1){//不应该进行这一步签到
            throw new RuntimeException("Cannot forward. Student's status is not 1.");
        }
        if (!acceptableLocation(latitute, longitute, l1, l2)){//不在签到范围内
            throw new RuntimeException("Cannot forward. Location is not acceptable.");
        }
        forwardCheckin(original, time, latitute, longitute);
        logger.info("Student: {} forwarded checkin for class: {}", studentId, classId);
        return true;
    }

    /**
     * Checkin step3 for a student.
     * @param studentId
     * @param classId
     * @param QRCode
     */
    public boolean doQR(String studentId, String classId, String QRCode) {
        LocalDateTime time = LocalDateTime.now();
        AttendanceMeta latest_record = attendanceMetaRepository.findFirstByAclass_IdOrderByDeadlineDesc(classId);
        String metaId = latest_record.getId();
        Attendance original = attendanceRepository.findByStudent_IdAndMeta_Id(studentId, metaId);
        if (!isInTime(classId, LocalDateTime.now())) {
            throw new RuntimeException("Cannot forward. Teacher has not started the class yet or has ended the " +
                    "class.");
        }
        if (original==null){//没有先进行签到码签到
            throw new RuntimeException("Cannot forward. Student has not checked in yet.");
        }
        if(original.getStatus()!=2){//不应该进行这一步签到
            throw new RuntimeException("Cannot forward. Student's status is not 2.");
        }
        if (!QRCodeUtils.qrInTime(QRCode)){
            throw new RuntimeException("Cannot forward. QRCode has expired.");
        }
        if (!QRCodeUtils.isMetaIdEqual(QRCode, metaId)){
            throw new RuntimeException("Cannot forward. Unknown QRCode.");
        }
        forwardCheckin(original, time, -1L, -1L);
        logger.info("Student: {} forwarded checkin for class: {}", studentId, classId);
        return true;
    }
}
