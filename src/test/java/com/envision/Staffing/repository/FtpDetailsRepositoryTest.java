package com.envision.Staffing.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.envision.Staffing.model.FtpDetails;

@RunWith(SpringRunner.class)
@DataJpaTest
public class FtpDetailsRepositoryTest {
	FtpDetails ftpDetails = new FtpDetails();

	@Autowired
	private FtpDetailsRepository repository;

	@Test
	public void testExample() {
		ftpDetails.setFileName("test");
		ftpDetails.setPassword("123");
		ftpDetails.setUsername("user");
		ftpDetails.setFileUrl(null);
		ftpDetails.setHost(null);
		ftpDetails.setDirPath(null);

		FtpDetails ftpDetails1 = this.repository.save(ftpDetails);

		String id = ftpDetails1.getId();

		Assert.assertNotNull(this.repository.save(ftpDetails));

		Assert.assertTrue(this.repository.existsById(id));

		this.repository.deleteJobDetailById(id);

		Assert.assertFalse(this.repository.existsById(id));

		this.repository.delete(ftpDetails);
		Assert.assertNotNull(this.repository.findAll());

	}
}
