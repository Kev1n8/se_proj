package com.codeisright.attendance.cache;

public class UserDto {
    private String id;
    private String password;

    public UserDto(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getUsername() {
        return id;
    }

    public void setUsername(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
