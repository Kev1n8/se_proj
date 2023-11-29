package com.codeisright.attendance.service;

import com.codeisright.attendance.data.Course;
import com.codeisright.attendance.data.Teacher;
import com.codeisright.attendance.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(String id) {
        return courseRepository.findById(id).orElse(null);
    }

    public Course getCoursesByCourseName(String courseName) {
        return courseRepository.findByCourseName(courseName);
    }

    public Course getCoursesByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode);
    }

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Course course) {
        Course existingCourse = courseRepository.findById(course.getId()).orElse(null);
        assert existingCourse != null;
        existingCourse.setCourseCode(course.getCourseCode());
        existingCourse.setCourseDescription(course.getCourseDescription());
        existingCourse.setName(course.getName());
        existingCourse.setTime(course.getTime());
        existingCourse.setTeacher(course.getTeacher());
        existingCourse.setLocation(course.getLocation());
        return courseRepository.save(existingCourse);
    }

    public void deleteCourse(String id) {
        courseRepository.deleteById(id);
    }

    public List<Course> getCoursesByTeacherName(String teacher) {
        return courseRepository.findByTeacher_Name(teacher);
    }

    public List<Course> getCoursesByLocation(String location) {
        return courseRepository.findByLocation(location);
    }
}
