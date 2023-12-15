package com.codeisright.attendance.repository;

import com.codeisright.attendance.data.Jwt;
import org.springframework.data.repository.CrudRepository;

public interface JwtRepository  extends CrudRepository<Jwt, String> {

}