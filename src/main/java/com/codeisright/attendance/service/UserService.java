package com.codeisright.attendance.service;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.repository.*;
import com.codeisright.attendance.utils.ImageUtils;
import com.codeisright.attendance.view.StudentInfo;
import com.codeisright.attendance.view.TeacherInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    protected final TeacherRepository teacherRepository;
    protected final AclassRepository aclassRepository;
    protected final EnrollmentRepository enrollmentRepository;
    protected final StudentRepository studentRepository;
    protected final AttendanceRepository attendanceRepository;
    protected final AttendanceMetaRepository attendanceMetaRepository;
    protected final CourseRepository courseRepository;

    @Autowired
    public UserService(TeacherRepository teacherRepository, AclassRepository aclassRepository,
                       EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
                       AttendanceRepository attendanceRepository, AttendanceMetaRepository attendanceMetaRepository,
                       CourseRepository courseRepository) {
        this.teacherRepository = teacherRepository;
        this.aclassRepository = aclassRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceMetaRepository = attendanceMetaRepository;
        this.courseRepository = courseRepository;
    }

    public List<Aclass> getClassByTeacherId(String teacherId) {
        return aclassRepository.findByTeacherId(teacherId);
    }

    /**
     * Get classes a student has been in.
     * @param studentId
     */
    public List<Aclass> getClassByStudentId(String studentId) {
        List<Enrollment> enrollments = enrollmentRepository.findAclassByStudent_Id(studentId);
        List<Aclass> classes = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            classes.add(enrollment.getAclass());
        }
        return classes;
    }

    /**
     * Get a class by its id or null if not exists.
     * @param id
     */
    public Aclass getClass(String id) {
        return aclassRepository.findById(id).orElse(null);
    }

    /**
     * Get a list of students in a class.
     * @param classId
     */
    public List<StudentInfo> getClassStudents(String classId) {
        List<Enrollment> lis = enrollmentRepository.findStudentByAclass_Id(classId);
        List<StudentInfo> students = new ArrayList<>();
        for (Enrollment e : lis) {
            students.add(studentRepository.findStudentInfoById(e.getStudent().getUsername()));
        }
        return students;
    }

    /**
     * Get a student by its id or null if not exists.
     * @param studentId
     */
    public StudentInfo getStudentInfo(String studentId) {
        return studentRepository.findStudentInfoById(studentId);
    }

    /**
     * Get bytes of a student's avatar or null if not exists.
     * @param teacherId
     */
    public byte[] getProfileAvatar(String teacherId) {
        // will return null if image not found. null then tell client to use default image
        return ImageUtils.getImageFromPath("src/main/resources/static/images/avatar/" + teacherId + ".jpg");
    }

    /**
     * Save the image to the server.
     * @param id
     * @param image
     */
    public void saveAvatar(String id, byte[] image) {
        ImageUtils.saveImage(image, "src/main/resources/static/images/avatar/" + id + ".jpg");
    }

    /**
     * Get the teacher by its id or null if not exists.
     * @param id
     */
    public Teacher getTeacherById(String id) {
        return teacherRepository.findById(id).orElse(null);
    }

    /**
     * Get a Meta by its id or null if not exists.
     * @param metaId
     */
    public AttendanceMeta getMetaByMetaId(String metaId) {
        return attendanceMetaRepository.findById(metaId).orElse(null);
    }

    /**
     * Get the course of a class.
     * @param classId
     */
    public Course getClassCourse(String classId) {
        try {
            return Objects.requireNonNull(aclassRepository.findById(classId).orElse(null)).getCourse();
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Get the class by its id.
     * @param classId
     */
    public Aclass getClassById(String classId) {
        return aclassRepository.findById(classId).orElseThrow(() -> new EntityNotFoundException("Error finding class " +
                "with id: " + classId));
    }

    /**
     * Get the teacher of a class.
     * @param teacherId
     */
    public TeacherInfo getTeacherByClassId(String teacherId) {
        return getClassById(teacherId).getTeacherInfo();
    }

    /**
     * Get the list of Meta of a class.
     * @param classId
     */
    public List<AttendanceMeta> getMetasByClassId(String classId) {
        return attendanceMetaRepository.findByAclass_Id(classId);
    }
}
