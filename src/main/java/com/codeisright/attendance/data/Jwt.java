package com.codeisright.attendance.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Jwt {
    @Id
    private String id;
    private String token;

    public Jwt() {}

    public Jwt(String id, String token) {
        this.id = id;
        this.token = token;
    }

    public String getId() {
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return null;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
