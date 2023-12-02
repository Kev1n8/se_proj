package com.codeisright.attendance.service;

import com.codeisright.attendance.data.*;
import com.codeisright.attendance.exception.EntityNotFoundException;
import com.codeisright.attendance.repository.*;
import com.codeisright.attendance.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class TeacherService {
    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);
    private final TeacherRepository teacherRepository;
    private final AClassRepository aclassRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMetaRepository attendanceMetaRepository;

    @Autowired
    public TeacherService(TeacherRepository teacherRepository, AClassRepository aclassRepository,
                          EnrollmentRepository enrollmentRepository, StudentRepository studentRepository,
                          AttendanceRepository attendanceRepository, AttendanceMetaRepository attendanceMetaRepository) {
        this.teacherRepository = teacherRepository;
        this.aclassRepository = aclassRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.attendanceMetaRepository = attendanceMetaRepository;
    }
//    private PasswordEncoder passwordEncoder;

//    @Configuration
//    class PasswordEncoder {
//        @Bean
//        public BCryptPasswordEncoder bCryptPasswordEncoder() {
//            return new BCryptPasswordEncoder();
//        }
//    }

//    @Autowired
//    public TeacherService(TeacherRepository teacherRepository, PasswordEncoder passwordEncoder) {
//        this.teacherRepository = teacherRepository;
//        this.passwordEncoder = passwordEncoder;
//    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public Teacher getTeacherById(String id) {
        return teacherRepository.findById(id).orElse(null);
    }

    public Teacher addTeacher(Teacher teacher) {
        logger.info("Teacher added: " + teacher);
        return teacherRepository.save(teacher);
    }

    public Teacher updateTeacher(Teacher teacher) {
        Teacher existingTeacher =
                teacherRepository.findById(teacher.getId()).orElseThrow(() -> new EntityNotFoundException("Teacher " +
                        "not found with ID: " + teacher.getId()));
        existingTeacher.setName(teacher.getName());
        existingTeacher.setAge(teacher.getAge());
        existingTeacher.setGender(teacher.getGender());
        existingTeacher.setDepartment(teacher.getDepartment());
        logger.info("Teacher updated: " + existingTeacher);
        return teacherRepository.save(existingTeacher);
    }

    public void deleteTeacher(String id) {
        logger.info("Teacher deleted with ID: " + id);
        teacherRepository.deleteById(id);
    }

    public List<Teacher> getTeachersByDepartment(String department) {
        return teacherRepository.findByDepartment(department);
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
        if (password.matches(teacher.getCodedpassword())) {
            logger.info("Teacher logged in: " + teacher);
            return teacher;
        }
        logger.info("Teacher password incorrect: " + teacher);
        return null;
    }

    public List<Aclass> getClasses(String id) {
        return aclassRepository.findByTeacherId(id);
    }

    public Aclass getClass(String id) {
        return aclassRepository.findById(id).orElse(null);
    }

    public List<Student> getClassStudents(String classId) {
        List<Enrollment> lis = enrollmentRepository.findStudentByAclass_Id(classId);
        List<Student> students = new ArrayList<>();
        for (Enrollment e : lis) {
            students.add(studentRepository.findById(e.getStudent().getId()).orElse(null));
        }
        return students;
    }

    public Student getStudentInfo(String studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }

    public byte[] getProfileAvatar(String teacherId) {
        // will return null if image not found. null then tell client to use default image
        return ImageUtils.getImageFromPath("src/main/resources/static/images/avatar/" + teacherId + ".jpg");
    }

    public List<Attendance> getAttendanceByClassId(String classId) {
        return attendanceRepository.findByAclass_Id(classId);
    }

    public AttendanceMeta getAttendanceMeta(String metaId) {
        return attendanceMetaRepository.findById(metaId).orElse(null);
    }

    public List<Student> getStudentsCheckinSuccess(String classId, String metaId){
        List<Attendance> records = attendanceRepository.findByAclass_IdAndMeta_Id(classId, metaId);
        List<Student> students = new ArrayList<>();
        int requirement = getAttendanceMeta(metaId).getRequirement();
        for (Attendance a : records) {
            if (a.getStatus()==requirement) {
                students.add(studentRepository.findById(a.getStudent().getId()).orElse(null));
            }
        }
        return students;
    }

    public List<Student> getStudentAbsent(String classId, String metaId){
        List<Student> all = getClassStudents(classId);
        List<Student> success = getStudentsCheckinSuccess(classId, metaId);
        all.removeAll(success);
        return all;
    }

    public List<List<Student>> getAttendanceCircumstance(String classId, String metaId) {
        List<Student> absentStudents = getStudentAbsent(classId, metaId);
        List<Student> success = getStudentsCheckinSuccess(classId, metaId);
        return new ArrayList<>(List.of(success, absentStudents));
    }

    public byte[] getClassExcel(String classId) {
        String path = "src/main/resources/static/excel/" + classId + ".xlsx";
        List<AttendanceMeta> metas = attendanceMetaRepository.findByAclass_Id(classId);
        return null;
    }

    public void saveAvatar(String id, byte[] image) {
        ImageUtils.saveImage(image, "src/main/resources/static/images/avatar/" + id + ".jpg");
    }

    public AttendanceMeta announce(String classId, AttendanceMeta meta){
        Aclass aclass = aclassRepository.findById(classId).orElse(null);
        if (aclass == null) {
            return null;
        }
        meta.setAclass(aclass);  // unnecessary but just in case
        return attendanceMetaRepository.save(meta);
    }

    public Aclass addClass(String teacherId, Aclass aclass) {
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            return null;
        }
        aclass.setTeacher(teacher);
        return aclassRepository.save(aclass);
    }

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

    public void deleteClass(String id) {
        aclassRepository.deleteById(id);
    }

    public void deleteAttendanceMeta(String id) {
        attendanceMetaRepository.deleteById(id);
    }

    public void deleteClassStudent(String classId, String studentId) {
        Enrollment enrollment = enrollmentRepository.findByAclass_IdAndStudent_Id(classId, studentId);
        enrollmentRepository.delete(enrollment);
    }
}
