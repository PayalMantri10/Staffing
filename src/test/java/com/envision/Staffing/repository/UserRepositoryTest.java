package com.envision.Staffing.repository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.envision.Staffing.model.User;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {

	User user = new User();

	@Autowired
	private UserRepository repository;

	@Test
	public void testExample() {
		String role = "ROLE_ADMIN";
		Set<String> roles = new HashSet<>(Arrays.asList(role));
		user.setEmail("a@gmail.com");
		user.setId("osatuser");
		user.setName("user");
		user.setPassword("123");
		user.setRoles(roles);
		Assert.assertFalse(this.repository.existsById("1234"));
	}
}
