package com.envision.Staffing.model;

import java.util.Set;

// Pojo created to match the format of user details used in UI
public class UserAuth {
	
	public UserAuth() {
		super();
	}
	
	private Set<String> roles;
	private String id;

	private String name;

	private String email;

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
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
	
}
