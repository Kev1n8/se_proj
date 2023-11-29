package com.codeisright.attendance.service;

import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

public interface ModifyService {
    /**
     * Add a class to the database.
     * @param title
     * @param description
     * @param grade
     * @param courseId
     * @param teacherId
     * @return true if the class is added successfully, false otherwise.
     */
    boolean addClass(String title, String description, int grade, String courseId, String teacherId);

    /**
     * Add a course to the database.
     * @param courseName
     * @param courseCode
     * @return true if the course is added successfully, false otherwise.
     */
    boolean addCourse(String courseName, String courseCode);

    /**
     * Add a teacher to the database.
     * @param teacherId
     * @param teacherName
     * @param age
     * @param gender
     * @param department
     * @param password
     * @return true if the teacher is added successfully, false otherwise.
     */
    boolean addTeacher(String teacherId, String teacherName, int age, String gender, String department, String password);

    /**
     * Add a published to the database.
     * @param requirement
     * @param startTime
     * @param classId
     * @param deadline
     * @return true if the published is added successfully, false otherwise.
     */
    boolean announceAttendance(int requirement, LocalDateTime startTime, LocalDateTime deadline, String classId);

    /**
     * Delete a class from the database.
     * @param classId
     * @return true if the class is deleted successfully, false otherwise.
     */
    boolean deleteClass(String classId);

    /**
     * Delete a course from the database.
     * @param courseName
     * @return true if the course is deleted successfully, false otherwise.
     */
    boolean deleteCourse(String courseName);

    /**
     * Delete a teacher from the database.
     * @param teacherId
     * @return true if the teacher is deleted successfully, false otherwise.
     */
    boolean deleteTeacher(String teacherId);

    /**
     * Delete a published from the database.
     * @param publishedId
     * @return true if the published is deleted successfully, false otherwise.
     */
    boolean deleteAnnouncement(String publishedId);

    /**
     * Modify a class in the database.
     * @param classId
     * @param title
     * @param grade
     * @return true if the class is modified successfully, false otherwise.
     */
    boolean modifyClass(String classId, String title, int grade);

    /**
     * Modify a course in the database.
     * @param courseName
     * @param courseCode
     * @return true if the course is modified successfully, false otherwise.
     */
    boolean modifyCourse(String courseName, String courseCode);

    /**
     * Modify a teacher in the database.
     * @param teacherId
     * @param name
     * @param department
     * @return true if the teacher is modified successfully, false otherwise.
     */
    boolean modifyTeacher(String teacherId, String name, String department);

    /**
     * Modify a published in the database.
     * @param publishedId
     * @param deadline
     * @return true if the published is modified successfully, false otherwise.
     */
    boolean modifyAnnouncement(String publishedId, LocalDateTime deadline);
}
