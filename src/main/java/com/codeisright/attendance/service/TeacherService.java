package com.codeisright.attendance.service;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.exception.EntityNotFoundException;
import com.codeisright.attendance.repository.*;
import com.codeisright.attendance.utils.RandomIdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public Teacher registerTeacher(String teacherId, String teacherName, int age, String gender, String department,
                                   String password) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher != null) {
            logger.info("Teacher already exists with ID: " + teacherId);
            return null;
        }
        teacher = new Teacher(teacherId, teacherName, age, gender, department, password);
        logger.info("Registering teacher: " + teacher);
        return addTeacher(teacher);
    }

    public Teacher loginTeacher(String teacherId, String password) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            logger.info("Teacher not found with ID: " + teacherId);
            return null;
        }
        if (password.matches(teacher.getPassword())) {
            logger.info("Teacher logged in: " + teacher);
            return teacher;
        }
        logger.info("Teacher password incorrect: " + teacher);
        return null;
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    /**
     * Add a new teacher.
     * @param teacher
     */
    public Teacher addTeacher(Teacher teacher) {
        logger.info("Teacher added: " + teacher);
        return teacherRepository.save(teacher);
    }

    /**
     * Update a teacher.
     * @param teacher
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
     * @param id
     */
    public void deleteTeacher(String id) {
        logger.info("Teacher deleted with ID: " + id);
        teacherRepository.deleteById(id);
    }

    /**
     * Get teachers in a department.
     * @param department
     */
    public List<Teacher> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
    }

    /**
     * Get students checkin successfully by metaId.
     * @param classId
     * @param metaId
     */
    public List<Student> getStudentsCheckinSuccess(String classId, String metaId) {
        List<Attendance> records = attendanceRepository.findByAclass_IdAndMeta_Id(classId, metaId);
        List<Student> students = new ArrayList<>();
        int requirement = getMetaByMetaId(metaId).getRequirement();
        for (Attendance a : records) {
            if (a.getStatus() == requirement) {
                students.add(studentRepository.findById(a.getStudent().getUsername()).orElse(null));
            }
        }
        return students;
    }

    /**
     * Get all students absent in a Meta.
     * @param classId
     * @param metaId
     */
    public List<Student> getStudentAbsent(String classId, String metaId) {
        List<Student> all = getClassStudents(classId);
        List<Student> success = getStudentsCheckinSuccess(classId, metaId);
        all.removeAll(success);
        return all;
    }

    /**
     * Give a list of two lists, the first list is the students who have checked in successfully,
     * the second list is the students who are absent.
     * @param classId
     * @param metaId
     */
    public List<List<Student>> getAttendanceCircumstance(String classId, String metaId) {
        List<Student> absentStudents = getStudentAbsent(classId, metaId);
        List<Student> success = getStudentsCheckinSuccess(classId, metaId);
        return new ArrayList<>(List.of(success, absentStudents));
    }

    /**
     *
     * @param classId
     * @return
     */
    public byte[] getClassExcel(String classId) {
        String path = "src/main/resources/static/excel/" + classId + ".xlsx";
        List<AttendanceMeta> metas = attendanceMetaRepository.findByAclass_Id(classId);
        return null;
    }

    /**
     * Announce an attendance.
     * The meta sent from frontend should not have an id.
     * But will include classId, requirement, location and time
     * @param classId
     * @param meta
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
     * Add a class to a teacher.
     * @param teacherId
     * @param aclass
     */
    public Aclass addClass(String teacherId, Aclass aclass) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            return null;
        }
        aclass.setTeacher(teacher);
        return aclassRepository.save(aclass);
    }

    /**
     * Update class information.
     * @param aclass
     * @return
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
     * @param meta
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
     * @param id
     */
    public void deleteClass(String id) {
        aclassRepository.deleteById(id);
    }

    /**
     * Withdraw an announcement.
     * @param id
     */
    public void deleteAttendanceMeta(String id) {
        attendanceMetaRepository.deleteById(id);
    }

    /**
     * Delete a student from a class.
     * @param classId
     * @param studentId
     */
    public void deleteClassStudent(String classId, String studentId) {
        Enrollment enrollment = enrollmentRepository.findByAclass_IdAndStudent_Id(classId, studentId);
        enrollmentRepository.delete(enrollment);
    }
}
