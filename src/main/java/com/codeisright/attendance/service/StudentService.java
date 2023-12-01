package com.codeisright.attendance.service;

import com.codeisright.attendance.data.Student;
import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class StudentService {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository){
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    public Student getStudentById(String id){
        return studentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Error finding student with id: "+id));
    }

    public Student updateStudent(Student student){
        Student existingStudent = studentRepository.findById(student.getId()).orElseThrow(() -> new EntityNotFoundException("Error finding student with id: "+student.getId()));
        existingStudent.setName(student.getName());
        existingStudent.setAge(student.getAge());
        existingStudent.setGender(student.getGender());
        return studentRepository.save(existingStudent);
    }

    public void deleteStudent(String id){
        studentRepository.deleteById(id);
    }

    public List<Student> getStudentByClass(String theclass){
        return studentRepository.findByTheclass(theclass);
    }

    public Student registerStudent(String id, String studentName, String codedPassword, int age, String gender, String major, String studentClass){
        Student toAdd = studentRepository.findById(id).orElse(null);
        if (toAdd != null){
            return null;
        }
        toAdd = new Student(id, studentName, codedPassword, age, gender, major, studentClass);
        return toAdd;
    }

    public Student loginStudent(String id, String codedPassword){
        //TODO: security configuration
        Student student = studentRepository.findById(id).orElse(null);
        if (student==null){
            return null;
        }
        if (student.getCodedpassword().matches(codedPassword)){
            return student;
        }
        return null;
    }
}
