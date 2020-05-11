package com.envision.Staffing.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.envision.Staffing.model.FileDetails;

@RunWith(SpringRunner.class)
@DataJpaTest
public class FileDetailsRepositoryTest {

	FileDetails fileDetails = new FileDetails();

	@Autowired
	private FileDetailsRepository repository;

	@Test
	public void testExample() {
		fileDetails.setFileExtension("xlsx");
		fileDetails.setDataFile(null);

		FileDetails fileDetails1 = this.repository.save(fileDetails);
		String id = fileDetails1.getId();

		Assert.assertNotNull(this.repository.save(fileDetails));

		Assert.assertTrue(this.repository.existsById(id));

		this.repository.deleteJobDetailById(id);

		Assert.assertFalse(this.repository.existsById(id));

		this.repository.delete(fileDetails);
		Assert.assertNotNull(this.repository.findAll());
	}
}
