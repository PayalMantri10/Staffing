package com.envision.Staffing.services;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JwtUtilTest {

	@Mock
	UserDetails userDetails;

	@InjectMocks
	JwtUtil jwtUtil;

	@Before
	public void setUp() {
		ReflectionTestUtils.setField(jwtUtil, "accessTokenExpiry", 3600000);
		ReflectionTestUtils.setField(jwtUtil, "refreshTokenExpiry", 172800000);
	}

	@Test
	public void TestUtil() {
		String token = jwtUtil.generateAccessToken(userDetails);
		Assert.assertNotNull(jwtUtil.generateAccessToken(userDetails));
		Assert.assertNotNull(jwtUtil.generateRefreshToken(userDetails));
		Assert.assertFalse(jwtUtil.isTokenExpired(token));
		jwtUtil.extractUsername(token);
	}
}
