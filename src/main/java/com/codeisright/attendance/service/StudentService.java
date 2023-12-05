package com.codeisright.attendance.service;

import com.codeisright.attendance.data.Aclass;
import com.codeisright.attendance.data.Enrollment;
import com.codeisright.attendance.data.Student;
import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.repository.AclassRepository;
import com.codeisright.attendance.repository.EnrollmentRepository;
import com.codeisright.attendance.repository.StudentRepository;
import com.codeisright.attendance.utils.ImageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;
    private final AclassRepository aclassRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, AclassRepository aclassRepository,
                          EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.aclassRepository = aclassRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(String id) {
        return studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Error finding student " +
                "with id: " + id));
    }

    public Student updateStudent(Student student) {
        Student existingStudent =
                studentRepository.findById(student.getUsername()).orElseThrow(() -> new EntityNotFoundException("Error " +
                        "finding student with id: " + student.getUsername()));
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());
        existingStudent.setGender(student.getGender());
        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(String id) {
        studentRepository.deleteById(id);
    }

    public List<Student> getStudentByClass(String theclass) {
        return studentRepository.findByTheclass(theclass);
    }

    public byte[] getProfileAvatar(String studentId) {
        // will return null if image not found. null then tell client to use default image
        return ImageUtils.getImageFromPath("src/main/resources/static/images/avatar/" + studentId + ".jpg");
    }

    public Student registerStudent(String id, String studentName, String codedPassword, int age, String gender,
                                   String major, String studentClass) {
        Student toAdd = studentRepository.findById(id).orElse(null);
        if (toAdd != null) {
            return null;
        }
        toAdd = new Student(id, studentName, codedPassword, age, gender, major, studentClass);
        return toAdd;
    }

    public Student loginStudent(String id, String codedPassword) {
        //TODO: security configuration
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return null;
        }
        if (student.getCodedpassword().matches(codedPassword)) {
            return student;
        }
        return null;
    }

    public List<Aclass> getClasses(String studentId) {
        List<Enrollment> enrollments =  enrollmentRepository.findAclassByStudent_Id(studentId);
        List<Aclass> classes = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            classes.add(enrollment.getAclass());
        }
        return classes;
    }

    public Aclass getAclassById(String id) {
        return aclassRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Error finding class " +
                "with id: " + id));
    }

    public Teacher getTeacherByClassId(String teacherId) {
        return getAclassById(teacherId).getTeacher();
    }
}
