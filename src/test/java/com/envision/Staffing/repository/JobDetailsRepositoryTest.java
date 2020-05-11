package com.envision.Staffing.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.envision.Staffing.model.FileDetails;
import com.envision.Staffing.model.FtpDetails;
import com.envision.Staffing.model.JobDetails;

@RunWith(SpringRunner.class)
@DataJpaTest
public class JobDetailsRepositoryTest {

	JobDetails jobDetails = new JobDetails();
	FileDetails inputFileDetails = new FileDetails();

	FtpDetails inputFtpDetails = new FtpDetails();
	FtpDetails outputFtpDetails = new FtpDetails();

	@Autowired
	private JobDetailsRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void test1() {

		Integer[] shiftPref = new Integer[] { 8, 6, 4 };

		inputFileDetails.setFileExtension("xlsx");
		inputFileDetails.setDataFile(null);

		jobDetails.setName("Test1");
		jobDetails.setCronExpression("0 0/1 * 1/1 * ? *");
		jobDetails.setInputFileDetails(inputFileDetails);
		jobDetails.setInputFormat("DATA_FILE");
		jobDetails.setInputFtpDetails(inputFtpDetails);
		jobDetails.setLowerUtilizationFactor((float) 0.85);
		jobDetails.setOutputEmailId("a@gmail.com");
		jobDetails.setOutputFormat("EMAIL");
		jobDetails.setOutputFtpDetails(outputFtpDetails);
		jobDetails.setShiftLengthPreferences(shiftPref);
		jobDetails.setStatus("SCHEDULED");
		jobDetails.setUpperUtilizationFactor((float) 1.10);
		jobDetails.setClinicians(null);

		this.entityManager.persist(jobDetails);

		JobDetails jobDetails1 = this.repository.save(jobDetails);
		String id = jobDetails1.getId();

		Assert.assertNotNull(id);
		Assert.assertNotNull(this.repository.save(jobDetails));

		Assert.assertNotNull(this.repository.getByIdLeftJoin(id));

		this.repository.deleteJobDetailById(id);
		Assert.assertFalse(this.repository.existsById(id));

		this.repository.delete(jobDetails);
		Assert.assertNotNull(this.repository.findAll());
	}
}
