package com.envision.Staffing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.envision.Staffing.model.User;

public interface UserPrincipalRepository extends JpaRepository<User, Long> {
	User findById(String s);
}
