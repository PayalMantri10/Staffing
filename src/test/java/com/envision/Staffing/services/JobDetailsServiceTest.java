package com.envision.Staffing.services;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.envision.Staffing.model.Clinician;
import com.envision.Staffing.model.FileDetails;
import com.envision.Staffing.model.FtpDetails;
import com.envision.Staffing.model.JobDetails;
import com.envision.Staffing.repository.JobDetailsRepository;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JobDetailsServiceTest {

	JobDetails jobDetails = new JobDetails();

	List<JobDetails> jobDetailsList = new LinkedList<>();
	List<Clinician> clinicians = new LinkedList<>();
	Clinician physician = new Clinician();
	Clinician app = new Clinician();
	Clinician scribe = new Clinician();
	FileDetails fileDetails = new FileDetails();
	FtpDetails ftpDetails = new FtpDetails();

	String id = "2ac0ed25-ed5e-4891-85e1-12acac13d3e6";

	@InjectMocks
	JobDetailsService JobDetailsService;

	@Mock
	JobDetailsRepository jobDetailsRepository;

	@Mock
	QuartzSchedulerService quartzSchedulerService;

	@Test
	public void getAllJobDetailsTest() {
		when(jobDetailsRepository.findAll()).thenReturn(jobDetailsList);
		Assert.assertEquals(JobDetailsService.getAllJobDetails(), jobDetailsList);
	}

	@Test
	public void getJobDetailsByIdTest() {
		when(jobDetailsRepository.getByIdLeftJoin(id)).thenReturn(jobDetails);
		Assert.assertEquals(JobDetailsService.getJobDetailsById(id), jobDetails);
	}

	@Test
	public void createOrUpdateJobDetailsTest() {
		byte[] file = null;
		Integer[] shiftPref = new Integer[] { 8, 6, 4 };

		fileDetails.setDataFile(file);
		fileDetails.setFileExtension("xlsx");
		fileDetails.setId("ce362521-5f94-4ede-bbc7-433f1c818e99");
		ftpDetails.setFileUrl("ftp://182.74.103.251/files/output.txt");
		ftpDetails.setPassword("test");
		ftpDetails.setUsername("test");


		physician.setCoefficient(0);
		physician.setCost(200);
		physician.setExpressions(null);
		physician.setName("physician");
		physician.setPatientsPerHour(1.2);
		physician.setCapacity(null);

		app.setCoefficient(0);
		app.setCost(100);
		app.setExpressions(null);
		app.setName("app");
		app.setPatientsPerHour(0.6);
		app.setCapacity(null);

		scribe.setCoefficient(0);
		scribe.setCost(60);
		scribe.setExpressions(null);
		scribe.setName("scribe");
		scribe.setPatientsPerHour(0.37);
		scribe.setCapacity(null);

		clinicians.add(scribe);
		clinicians.add(app);
		clinicians.add(physician);

		jobDetails.setId(id);
		jobDetails.setName("Test1");
		jobDetails.setCronExpression("0 0/1 * 1/1 * ? *");
		jobDetails.setInputFileDetails(fileDetails);
		jobDetails.setInputFormat("DATA_FILE");
		jobDetails.setInputFtpDetails(null);
		jobDetails.setLowerUtilizationFactor((float) 0.85);
		jobDetails.setOutputEmailId(null);
		jobDetails.setOutputFormat("FTP_URL");
		jobDetails.setOutputFtpDetails(ftpDetails);
		jobDetails.setShiftLengthPreferences(shiftPref);
		jobDetails.setStatus("SCHEDULED");
		jobDetails.setUpperUtilizationFactor((float) 1.10);
		jobDetails.setClinicians(clinicians);

		when(jobDetailsRepository.getByIdLeftJoin(id)).thenReturn(jobDetails);
		when(jobDetailsRepository.save(jobDetails)).thenReturn(jobDetails);
		quartzSchedulerService.scheduleJob(jobDetails);
		Assert.assertEquals(JobDetailsService.createOrUpdateJobDetails(jobDetails, eq(null)), jobDetails);

		Optional<JobDetails> jobDetails1 = jobDetailsRepository.findById(id);
		when(jobDetailsRepository.findById(id)).thenReturn(jobDetails1);
	}
}
