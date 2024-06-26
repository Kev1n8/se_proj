package com.codeisright.attendance.service;

import com.codeisright.attendance.dto.AclassDto;
import com.codeisright.attendance.dto.AttendanceDto;
import com.codeisright.attendance.dto.MetaDto;
import com.codeisright.attendance.data.*;
import com.codeisright.attendance.dto.TeacherDto;
import com.codeisright.attendance.repository.*;
import com.codeisright.attendance.utils.ExcelHandler;
import com.codeisright.attendance.utils.QRCodeUtils;
import com.codeisright.attendance.utils.RandomIdGenerator;
import com.codeisright.attendance.view.StudentInfo;
import com.codeisright.attendance.view.TeacherInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class TeacherService extends UserService {
    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);

    public TeacherService(TeacherRepository teacherRepository, AclassRepository aclassRepository,
                          EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
                          AttendanceRepository attendanceRepository, AttendanceMetaRepository attendanceMetaRepository,
                          CourseRepository courseRepository) {
        super(teacherRepository, aclassRepository, enrollmentRepository, studentRepository, attendanceRepository,
                attendanceMetaRepository, courseRepository);
    }

    public Teacher registerTeacher(Teacher teacher) {
        Teacher ifExists = teacherRepository.findById(teacher.getUsername()).orElse(null);
        if (ifExists != null) {
            logger.info("Teacher already exists with ID: " + teacher.getUsername());
            return null;
        }
        logger.info("Registering teacher: " + teacher);
        return addTeacher(teacher);
    }

    /**
     * Add a new teacher.
     * @param teacher the teacher to be added.
     */
    public Teacher addTeacher(Teacher teacher) {
        logger.info("Teacher added: " + teacher);
        return teacherRepository.save(teacher);
    }

    /**
     * Update a teacher.
     * @param teacher the teacher to be updated.
     * @return the updated teacher or null if the teacher does not exist.
     */
    public Teacher updateTeacher(String id, TeacherDto teacher) {
        Teacher existingTeacher =
                teacherRepository.findById(id).orElse(null);
        if (existingTeacher == null) {
            logger.info("Teacher not found with ID: " + id);
            return null;
        }
        existingTeacher.setName(teacher.getName());
        existingTeacher.setAge(teacher.getAge());
        existingTeacher.setGender(teacher.getGender());
        existingTeacher.setDepartment(teacher.getDepartment());
        logger.info("Teacher updated: " + existingTeacher);
        return teacherRepository.save(existingTeacher);
    }

    /**
     * Delete a teacher account.
     * @param id the ID of the teacher to be deleted.
     */
    public void deleteTeacher(String id) {
        logger.info("Teacher deleted with ID: " + id);
        teacherRepository.deleteById(id);
    }

    /**
     * Get info of a teacher by ID.
     * @param id the ID of the teacher.
     */
    public TeacherInfo getTeacherInfoById(String id) {
        return teacherRepository.findTeacherInfoById(id);
    }

    /**
     * Get students checkin successfully by metaId.
     * @param classId the classId of the class.
     * @param metaId the metaId of the attendance.
     */
    public List<StudentInfo> getStudentsCheckinSuccess(String classId, String metaId) {
        List<Attendance> records = attendanceRepository.findByAclass_IdAndMeta_Id(classId, metaId);
        List<StudentInfo> students = new ArrayList<>();
        int requirement = getMetaByMetaId(metaId).getRequirement();
        for (Attendance a : records) {
            if (a.getStatus() >= requirement) {
                students.add(studentRepository.findStudentInfoById(a.getStudent().getUsername()));
            }
        }
        return students;
    }

    /**
     * Get students checkin supplement by metaId.
     * @param classId the classId of the class.
     * @param metaId the metaId of the attendance.
     * @return a list of students who have checked in supplement.
     */
    public List<StudentInfo> getStudentsCheckinSupplement(String classId, String metaId) {
        List<Attendance> records = attendanceRepository.findByAclass_IdAndMeta_Id(classId, metaId);
        List<StudentInfo> students = new ArrayList<>();
        for (Attendance a : records) {
            if (a.getStatus() == -1) {
                students.add(studentRepository.findStudentInfoById(a.getStudent().getUsername()));
            }
        }
        return students;
    }

    /**
     * Give a list of 3 lists, the first list is the students who have checked in successfully,
     * the second list is the students who are absent,
     * the third list is the students who have checked in supplement-ly.
     * @param metaId the metaId of the attendance.
     */
    public List<List<StudentInfo>> getAttendanceCircumstance(String metaId) {
        AttendanceMeta target = attendanceMetaRepository.findById(metaId).orElse(null);
        if (target == null){
            return null;
        }
        String classId = target.getAclass().getId();

        List<StudentInfo> absentStudents = getClassStudents(classId);
        List<StudentInfo> success = getStudentsCheckinSuccess(classId, metaId);
        List<StudentInfo> supplement = getStudentsCheckinSupplement(classId, metaId);
        for (StudentInfo s : success) {  // not efficient, but works
            for (StudentInfo a : absentStudents) {
                if (s.getId().equals(a.getId())) {
                    absentStudents.remove(a);
                    break;
                }
            }
        }
        for (StudentInfo s : supplement) {
            for (StudentInfo a : absentStudents) {
                if (s.getId().equals(a.getId())) {
                    absentStudents.remove(a);
                    break;
                }
            }
        }
        return new ArrayList<>(List.of(success, absentStudents, supplement));
    }

    /**
     *  Get the attendance circumstance of a class.
     * @param classId  the classId of the class.
     * @return a list of attendance circumstance of the class.
     */
    public byte[] getClassExcel(String classId) {
        String path = "src/main/resources/static/excels/" + classId + ".xlsx";
        List<AttendanceMeta> metas = attendanceMetaRepository.findByAclass_IdOrderByDeadlineDesc(classId);
        if (metas == null || metas.size() == 0) {
            return null;
        }
        List<List<List<StudentInfo>>> circumstances = new ArrayList<>();
        for (AttendanceMeta meta : metas) {
            circumstances.add(0, getAttendanceCircumstance(meta.getId()));
        }
        logger.info("Done collecting records for class with id: " + classId);
        return ExcelHandler.save(path, metas, circumstances);
    }

    /**
     * Announce an attendance.
     * The meta sent from frontend should not have an id.
     * But will include classId, requirement, location and time
     * @param classId the classId of the class.
     * @param meta the meta of the attendance.
     */
    public AttendanceMeta announce(String classId, MetaDto meta) {
        Aclass aclass = aclassRepository.findById(classId).orElse(null);
        if (aclass == null) {
            return null;
        }

        String newId = RandomIdGenerator.generate();
        while(attendanceMetaRepository.findById(newId).isPresent()){ // make sure the id is unique
            newId = RandomIdGenerator.generate();
        }
        AttendanceMeta newMeta = new AttendanceMeta(newId, meta, aclass);

        logger.info("Attendance announced: " + newMeta);
        return attendanceMetaRepository.save(newMeta);
    }

    /**
     * Get real-time qr code for attendance with metaId.
     * @param metaId the metaId of the attendance.
     */
    public String getAttendanceQR(String metaId) {
        LocalDateTime now = LocalDateTime.now();
        AttendanceMeta meta = attendanceMetaRepository.findById(metaId).orElse(null);
        if (meta == null) {
            return "No such attendanceMeta.";
        }
        if (now.isBefore(meta.getStart()) || now.isAfter(meta.getDeadline())) {
            return "Attendance not available now.";
        }
        return QRCodeUtils.generateQRCode(metaId, 30L);  // 30 seconds alive
    }

    /**
     * Add a class to a teacher.
     * @param teacherId the ID of the teacher.
     * @param aclass the class to be added.
     */
    public Aclass addClass(String teacherId, AclassDto aclass) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        Course course = courseRepository.findById(aclass.getCourseId()).orElse(null);
        if (teacher == null || course == null) {
            return null;
        }
        Aclass newClass = new Aclass();
        newClass.setClass(aclass, course, teacher);
        logger.info("Class added: " + newClass);
        Aclass toReturn = aclassRepository.save(newClass);
        return toReturn;
    }

    /**
     * Clear up the metas of a class.
     *
     * @param classId id of target class
     * @return true if success, false if no such class or class has no metas
     */
    public boolean clearClassMetas(String classId) {
        List<AttendanceMeta> metas = attendanceMetaRepository.findAllByAclass_Id(classId);
        if (metas == null) {
            return false;
        }
        attendanceMetaRepository.deleteAll(metas);
        return true;
    }

    /**
     * Clear up the attendance records of a class.
     *
     * @param classId id of target class
     * @return true if success, false if no such class or class has no attendance records
     */
    public boolean clearClassAttendance(String classId) {
        List<Attendance> attendances = attendanceRepository.findByAclass_Id(classId);
        if (attendances == null) {
            return false;
        }
        attendanceRepository.deleteAll(attendances);
        return true;
    }

    /**
     * Clear up the enrollments of a class.
     *
     * @param classId id of target class
     * @return true if success, false if no such class or class has no enrollments
     */
    public boolean clearClassEnrollment(String classId) {
        List<Enrollment> enrollments = enrollmentRepository.findAllByAclass_Id(classId);
        if (enrollments == null) {
            return false;
        }
        enrollmentRepository.deleteAll(enrollments);
        return true;
    }

    /**
     * Pour students into class by Excel file and flush all things about the class.
     * Have to deal with potential problems like duplicate students, invalid students, etc.
     * So, divided the students into three lists: [valid], [invalid(not exists in Student)], [duplicate].
     * @param classId the classId of the class.
     * @param file the Excel file.
     */
    public List<Object> addClassStudentByExcel(String classId, byte[] file){
        Aclass clazz = aclassRepository.findById(classId).orElse(null);
        if (clazz==null){
            return null;
        }

        clearClassAttendance(classId);// delete all attendance records to prevent data inconsistency
        clearClassEnrollment(classId);
        clearClassMetas(classId);

        List<StudentInfo> valid = new ArrayList<>();
        List<String> invalid = new ArrayList<>();
        List<StudentInfo> duplicate = new ArrayList<>();
        List<String> students2Add = ExcelHandler.getStudentIds(file);

        for (String id : students2Add){
            Student student2Add = studentRepository.findById(id).orElse(null);
            if (student2Add == null){   // invalid
                invalid.add(id);
                continue;
            }
            StudentInfo info2Add = student2Add.toStudentInfo();
            if (valid.contains(info2Add)){//work or not ???
                if (!duplicate.contains(info2Add))
                    duplicate.add(info2Add);
            }
            else{
                Enrollment record = new Enrollment();
                record.setStudent(student2Add);
                record.setAclass(clazz);
                enrollmentRepository.save(record);
                valid.add(info2Add);
            }
        }
        return List.of(valid, invalid, duplicate);
    }

    /**
     * Update class information.
     * @param aclass the class to be updated.
     * @return the updated class.
     */
    public Aclass updateClass(String classId, AclassDto aclass) {
        Aclass existingClass = aclassRepository.findById(classId).orElse(null);
        if (existingClass == null) {
            logger.info("No such class.");
            return null;
        }
        existingClass.setTitle(aclass.getTitle());
        existingClass.setDescription(aclass.getDescription());
        existingClass.setGrade(aclass.getGrade());

        Teacher teacher = teacherRepository.findById(aclass.getTeacherId()).orElse(null);
        Course course = courseRepository.findById(aclass.getCourseId()).orElse(null);
        if (teacher==null || course==null){
            logger.info("No such teacher or course.");
            return null;
        }

        existingClass.setTeacher(teacher);
        existingClass.setCourse(course);
        return aclassRepository.save(existingClass);
    }

    /**
     * Update an announcement.
     * @param meta the announcement to be updated.
     */
    public AttendanceMeta updateAttendanceMeta(String metaId, MetaDto meta) {
        AttendanceMeta existingMeta = attendanceMetaRepository.findById(metaId).orElse(null);
        Aclass newClass = aclassRepository.findById(meta.getClassId()).orElse(null);
        if (existingMeta == null || newClass == null) {
            return null;
        }
        existingMeta.setAclass(newClass);
        existingMeta.setRequirement(meta.getRequirement());
        existingMeta.setStart(meta.getStart());
        existingMeta.setDeadline(meta.getDeadline());
        return attendanceMetaRepository.save(existingMeta);
    }

    /**
     * Delete a class.
     * @param id the id of the class.
     */
    public void deleteClass(String id) {
        aclassRepository.deleteById(id);
    }

    /**
     * Withdraw an announcement.
     * @param id the id of the announcement.
     */
    public void deleteAttendanceMeta(String id) {
        attendanceMetaRepository.deleteById(id);
    }

    /**
     * Delete a student from a class.
     * @param classId the id of the class.
     * @param studentId the id of the student.
     */
    public void deleteClassStudent(String classId, String studentId) {
        Enrollment enrollment = enrollmentRepository.findByAclass_IdAndStudent_Id(classId, studentId);
        enrollmentRepository.delete(enrollment);
    }

    /**
     * Check if the student is in the class.
     * @param classId the id of the class.
     * @param studentId the id of the student.
     * @return true if the student in class, false otherwise.
     */
    public boolean isStudentInClass(String classId, String studentId) {
        Enrollment enrollment = enrollmentRepository.findByAclass_IdAndStudent_Id(classId, studentId);
        return enrollment != null;
    }

    /**
     * Check if there are notification to send.  not notified and finished
     * @param teacherId id of the teacher
     * @return true if there are notification to send, false otherwise.
     */
    public List<AttendanceMeta> getNotification(String teacherId) {
        List<Aclass> properties = aclassRepository.findByTeacherId(teacherId);
        List<AttendanceMeta> toNotify = new ArrayList<>();
        for (Aclass clazz : properties){
            List<AttendanceMeta> item = attendanceMetaRepository.findByAclass_IdAndNotifiedIsFalseAndDeadlineBefore(clazz.getId(), LocalDateTime.now());
            toNotify.addAll(item);
            for (AttendanceMeta meta : item){
                meta.setNotified(true);
                attendanceMetaRepository.save(meta);
            }
        }
        if (toNotify.size()==0)
            return null;
        toNotify.sort(Comparator.comparing(AttendanceMeta::getDeadline).reversed());
        return toNotify;
    }

    /**
     * Teacher change password.
     * @param teacherId the id of the teacher.
     * @param password the new password.
     * @return Teacher if success, null otherwise.
     */
    public Teacher updatePassword(String teacherId, String password) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            return null;
        }
        teacher.setPassword(password);
        return teacherRepository.save(teacher);
    }

    /**
     * Make up for the absence of a student.
     *
     * @param attendanceDto dto contains studentId, metaId
     * @return Attendance if success, null otherwise.
     */
    public Attendance makeUpAttendance(AttendanceDto attendanceDto){
        String studentId = attendanceDto.getStudentId();
        String metaId = attendanceDto.getMetaId();
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null){
            return null;
        }
        AttendanceMeta target = attendanceMetaRepository.findById(metaId).orElse(null);
        if (target == null){
            return null;
        }
        String classId = target.getAclass().getId();
        Aclass clazz = aclassRepository.findById(classId).orElse(null);
        if (clazz==null){
            return null;
        }
        Enrollment enrollment = enrollmentRepository.findByAclass_IdAndStudent_Id(classId, studentId);
        if (enrollment == null){
            return null;
        }
        Attendance attendance = new Attendance(student, clazz, target, -1, LocalDateTime.now(), -1L, -1L);
        return attendanceRepository.save(attendance);
    }
}
