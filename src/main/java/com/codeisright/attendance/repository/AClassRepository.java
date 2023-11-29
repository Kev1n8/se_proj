package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.AClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AClassRepository extends JpaRepository<AClass, String> {

    public List<AClass> findAClassByTitle(String title);

    public List<AClass> findAClassByGrade(int grade);
}
