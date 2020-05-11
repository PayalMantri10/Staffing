package com.envision.Staffing.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.envision.Staffing.controllers.UserController;
import com.envision.Staffing.model.User;
import com.envision.Staffing.services.UserService;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

	User user = new User();
	List<User> userList = new LinkedList<>();

	@InjectMocks
	UserController userController;

	@Mock
	UserService userService;

	@Test
	public void addNewUserTest() {
		when(userService.createOrUpdateUser(any(User.class))).thenReturn(user);
		Assert.assertEquals(userController.addNewUser("osatadmin", "a@gmail.com"), "Saved");
	}

	@Test
	public void getAllUsersTest() {
		when(userService.getAllUsers()).thenReturn(userList);
		Assert.assertEquals(userController.getAllUsers(), userList);
	}

}
