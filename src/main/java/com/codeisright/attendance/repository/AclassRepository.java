package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Aclass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AclassRepository extends JpaRepository<Aclass, String> {

    List<Aclass> findByTeacherId(String teacherId);

}
