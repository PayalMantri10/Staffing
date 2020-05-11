package com.envision.Staffing.services;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.envision.Staffing.model.User;
import com.envision.Staffing.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	User user = new User();
	List<User> userList = new LinkedList<User>();
	Optional<User> user1;

	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	@Test
	public void getAllUsersTest() {
		when(userRepository.findAll()).thenReturn(userList);
		Assert.assertEquals(userService.getAllUsers(), userList);
	}

	@Test
	public void createOrUpdateUserTest() {
		when(userRepository.save(user)).thenReturn(user);
		Assert.assertEquals(userService.createOrUpdateUser(user), user);
	}

}
