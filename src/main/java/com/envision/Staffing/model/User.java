package com.envision.Staffing.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity // This tells Hibernate to make a table out of this class
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

	public User() {
		super();
	}

	@Transient
	private Set<String> roles;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;

	private String name;

	private String email;

	private String password;

	public User(String name, Set<String> roles, String password) {
		super();
		this.name = name;
		this.roles = roles;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
}
