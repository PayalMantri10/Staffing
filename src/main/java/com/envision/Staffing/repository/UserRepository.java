package com.envision.Staffing.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.envision.Staffing.model.User;

@Repository
public interface UserRepository extends CrudRepository<User,String> {
}
