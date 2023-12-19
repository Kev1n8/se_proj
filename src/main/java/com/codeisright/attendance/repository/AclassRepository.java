package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Aclass;
import com.codeisright.attendance.view.AclassInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AclassRepository extends JpaRepository<Aclass, String> {
    List<Aclass> findByTeacherId(String teacherId);

    List<AclassInfo> findAclassInfoByTeacherId(String teacherId);

    AclassInfo findAclassInfoById(String id);
}
