package com.codeisright.attendance.service;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.dto.StudentDto;
import com.codeisright.attendance.dto.StudentMetaRecord;
import com.codeisright.attendance.repository.*;
import com.codeisright.attendance.utils.QRCodeUtils;
import com.codeisright.attendance.view.StudentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class StudentService extends UserService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(TeacherRepository teacherRepository, AclassRepository aclassRepository,
                          EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
                          AttendanceRepository attendanceRepository, AttendanceMetaRepository attendanceMetaRepository,
                          CourseRepository courseRepository) {
        super(teacherRepository, aclassRepository, enrollmentRepository, studentRepository, attendanceRepository,
                attendanceMetaRepository, courseRepository);
    }

    /**
     * Get a student info by id.
     *
     * @param id student id
     * @return student info or null if not found
     */
    public StudentInfo getStudentInfoById(String id) {
        logger.info("Getting student with ID: " + id);
        return studentRepository.findStudentInfoById(id);
    }

    /**
     * Get a student by id.
     *
     * @param id student id
     * @return Student or null if not found
     */
    public Student getStudentById(String id) {
        return studentRepository.findById(id).orElse(null);
    }

    /**
     * Update a student account in the database.
     *
     * @param studentId student id
     * @param student student dto
     * @return Student or null if not found
     */
    public Student updateStudent(String studentId, StudentDto student) {
        Student existingStudent = studentRepository.findById(studentId).orElse(null);
        if (existingStudent == null) {
            logger.info("Student not found with ID: " + studentId);
            return null;
        }
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());
        existingStudent.setGender(student.getGender());
        return studentRepository.save(existingStudent);
    }

    /**
     * Change password of a student account.
     *
     * @param studentId id of the student
     * @param password new password
     * @return Student
     */
    public Student updatePassword(String studentId, String password) {
        Student existingStudent =
                studentRepository.findById(studentId).orElse(null);
        if (existingStudent == null) {
            logger.info("Student not found with ID: " + studentId);
            return null;
        }
        existingStudent.setPassword(password);
        return studentRepository.save(existingStudent);
    }

    /**
     * Delete a student account from the database.
     *
     * @param id student id
     */
    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }

    /**
     * Add a new student to the database.
     *
     * @param student student to add
     * @return Student or null if already exists
     */
    public Student registerStudent(Student student) {
        Student toAdd = studentRepository.findById(student.getUsername()).orElse(null);
        if (toAdd != null) {
            logger.info("Student already exists with ID: " + student.getUsername());
            return null;
        }
        return studentRepository.save(student);
    }

    /**
     * Get the attendance record of a student in a single metaAtt(if at least checkin step1 successfully before).
     *
     * @param studentId student id
     * @param attendanceMetaId attendance meta id
     * @return Attendance or null if not found
     */
    public Attendance getAttendanceByStudentAndMeta(String studentId, String attendanceMetaId) {
        return attendanceRepository.findByStudent_IdAndMeta_Id(studentId, attendanceMetaId);
    }

    /**
     * Get a page of Student records(metas with boolean) of a class. PageSize=10
     * Only when the meta has ended or the student has record(whether the status is -1 or 123)
     * will it be returned.
     *
     * @param classId the id of the class.
     * @param studentId the id of the student.
     * @param page the page number.
     * @return a page of Student records(metas with boolean) of a class.
     */
    public Page<StudentMetaRecord> getStudentRecords(String classId, String studentId, int page){
        logger.debug("Getting student's records of a class. Page : " + page);
        List<AttendanceMeta> all = getMetasByClassId(classId);
        List<StudentMetaRecord> content = new ArrayList<>();
        for (AttendanceMeta item : all){
            Attendance record = getAttendanceByStudentAndMeta(studentId, item.getId());
            if (record == null){
                continue;
            }
            content.add(new StudentMetaRecord(item, record));
        }

        int start = page * 10;
        int end = Math.min(start + 10, content.size());
        return new PageImpl<>(content.subList(start, end), PageRequest.of(page, 10), content.size());
    }

    /**
     * Return if the student is in the class.
     *
     * @param studentId student id
     * @param classId class id
     * @return true if the student is in the class, false otherwise
     */
    private boolean inClass(String studentId, String classId) {
        return enrollmentRepository.findByAclass_IdAndStudent_Id(classId, studentId) != null;
    }

    /**
     * Add a new checkin record to the attendance table.
     * Only used when a student checkin step1 successfully.
     *
     * @param studentId student id
     * @param classId class id
     * @param time checkin time
     * @return true if successfully added, false otherwise
     */
    public boolean addCheckin(String studentId, String classId, String metaId, LocalDateTime time) {
        Student student = studentRepository.findById(studentId).orElse(null);
        Aclass aClass = aclassRepository.findById(classId).orElse(null);
        AttendanceMeta meta = attendanceMetaRepository.findById(metaId).orElse(null);

        if (student == null || aClass == null || meta == null) {
            logger.error("Error adding checkin record: student or class or meta not found");
            return false;
        }

        Attendance toAdd = new Attendance(student, aClass, meta, 1,  time, null, null);
        attendanceRepository.save(toAdd);
        return true;
    }

    /**
     * Forward the latest checkin record to the current time.
     * If the input location values are -1, it's step 3, means remaining the original location.
     * Otherwise, it's step3 checkin. Ignore the input location values.
     *
     * @param recordToForward the latest checkin record
     * @param time            current time
     * @param latitude       latitude of the current location
     * @param longitude     longitude of the current location
     */
    public boolean forwardCheckin(Attendance recordToForward, LocalDateTime time, Long latitude, Long longitude) {
        if (recordToForward == null) {
            logger.error("Error forwarding checkin record:" + recordToForward);
            return false;
        }
        recordToForward.setTime(time);
        recordToForward.forward();
        if (latitude != -1 && longitude != -1) {  // step 2 checkin
            recordToForward.setLatitude(latitude);
            recordToForward.setLongitude(longitude);
        }
        attendanceRepository.save(recordToForward);
        return true;
    }

    /**
     * Calculate the distance(meters) between two locations.
     *
     * @param lat1 latitude of location1
     * @param lon1 longitude of location1
     * @param lat2 latitude of location2
     * @param lon2 longitude of location2
     * @return distance between two locations in kilometers
     */
    public double calculateDistance(Long lat1, Long lon1, Long lat2, Long lon2) {
        int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // convert to kilometers
    }

    /**
     * Check if the location is acceptable, which means the distance between the two locations should be less than 100m.
     *
     * @param latitude1  latitude of location1
     * @param longitude1  longitude of location1
     * @param latitude2  latitude of location2
     * @param longitude2  longitude of location2
     * @return true if the distance is less than 100m, false otherwise
     */
    public boolean acceptableLocation(Long latitude1, Long longitude1, Long latitude2, Long longitude2) {
        double distance = calculateDistance(latitude1, longitude1, latitude2, longitude2);
        return distance < 0.1;
    }

    /**
     * Check if the current time is in the time range of the latest attendance meta.
     *
     * @param metaId attendance meta id
     * @param time current time
     * @return true if the current time is in the time range of the latest attendance meta, false otherwise
     */
    public boolean isInTime(String metaId, LocalDateTime time) {
        AttendanceMeta latest_record = attendanceMetaRepository.findById(metaId).orElse(null);
        if (latest_record == null) {
            return false;
        }
        LocalDateTime latest_time = latest_record.getDeadline();
        return latest_time.isAfter(time);
    }

    /**
     * Checkin step1 for a student.
     *
     * @param studentId student id
     * @param metaId attendance meta id
     * @return 0 if successfully checked in, 1 if the student is not enrolled in the class, 2 if hove not started or
     * already ended, 3 if the student has already checked in, 4 for unknown meta
     * 5 for unknown error
     */
    public int doCheckin(String studentId, String metaId) {
        AttendanceMeta meta = attendanceMetaRepository.findById(metaId).orElse(null);
        if (meta == null) {
            return 4;
        }
        String classId = meta.getAclass().getId();
        if(!inClass(studentId, classId)) {
            return 1;
        }
        Attendance record = attendanceRepository.findByStudent_IdAndMeta_Id(studentId, metaId);
        if (record != null) {
            return 3;
        }
        LocalDateTime time = LocalDateTime.now();
        attendanceRepository.findByStudent_IdAndMeta_Id(studentId, metaId);
        if (!isInTime(metaId, time)) {
            return 2;
        }
        if(addCheckin(studentId, classId, metaId, time)) {
            logger.info("Student: {} checked in for class: {}, meta:{}", studentId, classId, metaId);
            return 0;
        }
        return 5;
    }

    /**
     * Checkin step2 for a student.
     *
     * @param studentId student id
     * @param metaId attendance meta id
     * @param latitute latitude of the current location
     * @param longitute longitude of the current location
     * @return 0 if successfully checked in, 1 if meta not exists, 2 if student hasn't done step1 checkin(status not 1),
     * 3 if not in time, 4 if not acceptable location, 5 for no need to do location
     */
    public int doLocation(String studentId, String metaId, Long latitute, Long longitute) {
        LocalDateTime time = LocalDateTime.now();
        AttendanceMeta latest_record = attendanceMetaRepository.findById(metaId).orElse(null);
        if (latest_record == null) {
            return 1;
        }
        Attendance original = attendanceRepository.findByStudent_IdAndMeta_Id(studentId, metaId);
        if(original == null) {
            return 2;
        }
        if(original.getStatus()>=latest_record.getRequirement()){
            return 5;
        }
        if(original.getStatus() != 1) {
            return 2;
        }
        Long l1 = latest_record.getLatitude();
        Long l2 = latest_record.getLongitude();

        if (!isInTime(metaId, LocalDateTime.now())) {
            return 3;
        }
        if (!acceptableLocation(latitute, longitute, l1, l2)) {//不在签到范围内
            return 4;
        }
        if (forwardCheckin(original, time, latitute, longitute)){
            logger.info("Student: {} forwarded checkin for metaId: {}", studentId, metaId);
            return 0;
        }
        return 6;
    }

    /**
     * Checkin step3 for a student.
     *
     * @param studentId student id
     * @param metaId meta id
     * @param QRCode QRCode
     * @return 0 if successfully checked in, 1 if meta not exists, 2 if student hasn't done step2 checkin(status not 2),
     * 3 if timeout, 4 if qr has expired, 5 if unknown qr, 6 if already checkin, 7 for unknown error
     */
    public int doQR(String studentId, String metaId, String QRCode) {
        LocalDateTime time = LocalDateTime.now();
        AttendanceMeta latest_record = attendanceMetaRepository.findById(metaId).orElse(null);
        if (latest_record == null) {
            return 1;
        }
        Attendance original = attendanceRepository.findByStudent_IdAndMeta_Id(studentId, metaId);
        if (original == null) {
            return 2;
        }
        if (original.getStatus()>=latest_record.getRequirement()){
            return 6;
        }
        if (original.getStatus() != 2) {
            return 2;
        }
        if (!isInTime(metaId, LocalDateTime.now())) {
            return 3;
        }
        if (!QRCodeUtils.qrInTime(QRCode)) {
            return 4;
        }
        if (!QRCodeUtils.isMetaIdEqual(QRCode, metaId)) {
            return 5;
        }
        if(forwardCheckin(original, time, -1L, -1L)){
            logger.info("Student: {} forwarded checkin for meta: {}", studentId, metaId);
            return 0;
        }
        return 7;
    }
}
