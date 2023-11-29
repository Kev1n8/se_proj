package com.codeisright.attendance.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class ToDo {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String title;

    private Boolean completed;

    protected ToDo() {}

    public ToDo(String title, Boolean completed){
        this.completed = completed;
        this.title = title;
    }

    public Long getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Boolean getCompleted() {
        return completed;
    }
}
