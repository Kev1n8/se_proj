package com.codeisright.attendance.service;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.exception.EntityNotFoundException;
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
import java.util.ArrayList;
import java.util.List;

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
     */
    public Teacher updateTeacher(Teacher teacher) {
        Teacher existingTeacher =
                teacherRepository.findById(teacher.getUsername()).orElseThrow(() -> new EntityNotFoundException(
                        "Teacher " +
                                "not found with ID: " + teacher.getUsername()));
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
     * Give a list of two lists, the first list is the students who have checked in successfully,
     * the second list is the students who are absent.
     * @param classId the classId of the class.
     * @param metaId the metaId of the attendance.
     */
    public List<List<StudentInfo>> getAttendanceCircumstance(String classId, String metaId) {
        List<StudentInfo> absentStudents = getClassStudents(classId);
        List<StudentInfo> success = getStudentsCheckinSuccess(classId, metaId);
        for (StudentInfo s : success) {  // not efficient, but works
            for (StudentInfo a : absentStudents) {
                if (s.getId().equals(a.getId())) {
                    absentStudents.remove(a);
                    break;
                }
            }
        }
        return new ArrayList<>(List.of(success, absentStudents));
    }

    /**
     *  Get the attendance circumstance of a class.
     * @param classId  the classId of the class.
     * @return a list of attendance circumstance of the class.
     */
    public byte[] getClassExcel(String classId) {
        String path = "src/main/resources/static/excels/" + classId + ".xlsx";
        List<AttendanceMeta> metas = attendanceMetaRepository.findByAclass_Id(classId);
        if (metas == null || metas.size() == 0) {
            return null;
        }
        List<List<List<StudentInfo>>> circumstances = new ArrayList<>();
        for (AttendanceMeta meta : metas) {
            circumstances.add(getAttendanceCircumstance(classId, meta.getId()));
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
    public AttendanceMeta announce(String classId, AttendanceMeta meta) {
        Aclass aclass = aclassRepository.findById(classId).orElse(null);
        if (aclass == null) {
            return null;
        }
        meta.setId(RandomIdGenerator.generate());
        return attendanceMetaRepository.save(meta);
    }

    /**
     * Get real-time qr code for attendance with metaId.
     * @param metaId the metaId of the attendance.
     */
    public String getAttendanceQR(String metaId) {
        LocalDateTime now = LocalDateTime.now();
        AttendanceMeta meta = attendanceMetaRepository.findById(metaId).orElse(null);
        if (meta == null) {
            return null;
        }
        if (now.isBefore(meta.getStart()) || now.isAfter(meta.getDeadline())) {
            return null;
        }
        return QRCodeUtils.generateQRCode(metaId, 30L);  // 30 seconds alive
    }

    /**
     * Add a class to a teacher.
     * @param teacherId the ID of the teacher.
     * @param aclass the class to be added.
     */
    public Aclass addClass(String teacherId, Aclass aclass) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            return null;
        }
        Aclass newClass = new Aclass(aclass);
        return aclassRepository.save(newClass);
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
                Enrollment record = new Enrollment(student2Add, clazz);
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
    public Aclass updateClass(Aclass aclass) {
        Aclass existingClass = aclassRepository.findById(aclass.getId()).orElse(null);
        if (existingClass == null) {
            return null;
        }
        existingClass.setTitle(aclass.getTitle());
        existingClass.setDescription(aclass.getDescription());
        existingClass.setTeacher(aclass.getTeacher());
        existingClass.setCourse(aclass.getCourse());
        existingClass.setGrade(aclass.getGrade());
        return aclassRepository.save(existingClass);
    }

    /**
     * Update an announcement.
     * @param meta the announcement to be updated.
     */
    public AttendanceMeta updateAttendanceMeta(AttendanceMeta meta) {
        AttendanceMeta existingMeta = attendanceMetaRepository.findById(meta.getId()).orElse(null);
        if (existingMeta == null) {
            return null;
        }
        existingMeta.setRequirement(meta.getRequirement());
        existingMeta.setAclass(meta.getAclass());
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
     * @param classId id of the class
     * @return true if there are notification to send, false otherwise.
     */
    public AttendanceMeta getNotification(String classId) {
        AttendanceMeta toNotify = attendanceMetaRepository.findFirstByAclass_IdAndNotifiedIsFalseAndDeadlineBefore(classId, LocalDateTime.now());
        if (toNotify==null)
            return null;
        toNotify.setNotified(true);
        attendanceMetaRepository.save(toNotify);
        return toNotify;
    }
}
