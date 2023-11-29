package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDo, Long>{

}
