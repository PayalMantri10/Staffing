package com.envision.Staffing.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.envision.Staffing.controllers.LoginController;
import com.envision.Staffing.model.User;
import com.envision.Staffing.services.JwtUtil;
import com.envision.Staffing.services.MyUserDetailsService;

@RunWith(MockitoJUnitRunner.Silent.class)
public class LoginControllerTest {

	User user = new User();

	UserDetails userDetails;

	@InjectMocks
	LoginController loginController;

	@Mock
	MyUserDetailsService userDetailsService;

	@Mock
	AuthenticationManager authenticationManager;

	@Mock
	JwtUtil jwtTokenUtil;

	@Test
	public void createAuthenticationTokenTest() throws Exception {

		String accessToken = null;
		String refreshToken = null;
		Set<String> roles = new HashSet<>(Arrays.asList("ROLE_ADMIN"));
		user.setEmail("a@g.com");
		user.setId("osatadmin");
		user.setName("ADMIN");
		user.setPassword("osatadmin123");
		user.setRoles(roles);

		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("osatadmin", "osatadmin123"));
		when(userDetailsService.loadUserByUsername("osatadmin")).thenReturn(userDetails);
		when(jwtTokenUtil.generateAccessToken(userDetails)).thenReturn(accessToken);
		when(jwtTokenUtil.generateRefreshToken(userDetails)).thenReturn(refreshToken);
	}

	@Test
	public void sendRefreshTokenTest() throws Exception {

		String accessToken = null;
		String refreshToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvc2F0YWRtaW4iLCJqdGkiOiJmOThhYzk3Ny04NjQ2LTQ5ODUtOWNjMy0yMmE1ZThhY2I5MzciLCJpYXQiOjE1ODU0NTcxMjQsImV4cCI6MTU4NTYzMzUyNH0.NR3Z6wDH5qLAK0tC2NkmmUBW6ckJyw9Nku-ugv5Mq1-T-q-_q8YCMCipt2zGia6k_o8oomUhMJiP3jO7hdMNug";

		when(userDetailsService.loadUserByUsername("osatadmin")).thenReturn(userDetails);
		when(jwtTokenUtil.generateAccessToken(userDetails)).thenReturn(accessToken);

		Assert.assertEquals(loginController.sendRefreshToken(refreshToken, "osatadmin1"),
				new ResponseEntity(HttpStatus.UNAUTHORIZED));
	}

}
