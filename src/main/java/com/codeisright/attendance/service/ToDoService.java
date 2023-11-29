package com.codeisright.attendance.service;

import com.codeisright.attendance.data.ToDo;
import com.codeisright.attendance.exception.EntityNotFoundException;
import com.codeisright.attendance.repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ToDoService {

    @Autowired
    private ToDoRepository toDoRepository;

    public List<ToDo> findAll(){
        return toDoRepository.findAll();
    }

    public ToDo findById(Long id){
        return toDoRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("ToDo not found with ID: "+id));
    }

    public ToDo save(ToDo toDo){
        return toDoRepository.save(toDo);
    }

    public void deleteById(Long id){
        toDoRepository.deleteById(id);
    }
}
