package com.envision.Staffing.services;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.envision.Staffing.model.User;
import com.envision.Staffing.model.UserAuth;
import com.envision.Staffing.model.UserPrincipal;
import com.envision.Staffing.repository.UserPrincipalRepository;

@RunWith(MockitoJUnitRunner.class)
public class MyUserDetailsServiceTest {

	User user = new User();
	UserAuth userAuth = new UserAuth();
	UserPrincipal userPrincipal = new UserPrincipal(user);

	@Mock
	UserPrincipalRepository userPrincipalRepository;

	@InjectMocks
	MyUserDetailsService myUserDetailsService;

	@Test
	public void loadUserByUsernameTest() {
		user.setName("ADMIN");
		user.setId("osatadmin");
		user.setPassword("123");
		when(userPrincipalRepository.findById("osatadmin")).thenReturn(user);
		myUserDetailsService.loadUserByUsername("osatadmin");
	}

	@Test
	public void getUserTest() {
		String role = "ROLE_ADMIN";
		Set<String> roles = new HashSet<>(Arrays.asList(role));

		user.setName("ADMIN");
		user.setId("osatadmin");
		user.setEmail("a@gmail.com");
		user.setPassword("123");
		user.setRoles(roles);

		userAuth.setEmail("a@gmail.com");
		userAuth.setId(null);
		userAuth.setName("osatadmin");
		userAuth.setRoles(roles);

		when(userPrincipalRepository.findById("osatadmin")).thenReturn(user);
		UserAuth userAuth1 = myUserDetailsService.getUser("osatadmin");
		Assert.assertEquals(userAuth1.getName(), userAuth.getName());
	}

}
