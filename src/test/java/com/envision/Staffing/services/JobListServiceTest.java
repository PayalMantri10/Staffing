package com.envision.Staffing.services;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.envision.Staffing.model.FileDetails;
import com.envision.Staffing.model.FtpDetails;
import com.envision.Staffing.model.JobDetails;
import com.envision.Staffing.repository.FileDetailsRepository;
import com.envision.Staffing.repository.FtpDetailsRepository;
import com.envision.Staffing.repository.JobDetailsRepository;

@RunWith(MockitoJUnitRunner.class)
public class JobListServiceTest {

	JobDetails jobDetails = new JobDetails();
	FileDetails fileDetails = new FileDetails();
	FtpDetails ftpDetails = new FtpDetails();

	@InjectMocks
	JobListService jobListService;

	@Mock
	private FileDetailsRepository fileDetailsRepository;

	@Mock
	private FtpDetailsRepository ftpDetailsRepository;

	@Mock
	private JobDetailsRepository jobDetailsRepository;

	@Mock
	private JobDetailsService jobDetailsService;

	@Mock
	private Scheduler scheduler;

	@Test
	public void deleteJobByIdTest() throws SchedulerException {
		String id = "2ac0ed25-ed5e-4891-85e1-12acac13d3e6";
		byte[] file = "hello".getBytes();
		Integer[] shiftPref = new Integer[] { 8, 6, 4 };

		fileDetails.setDataFile(file);
		fileDetails.setFileExtension("xlsx");
		fileDetails.setId("ce362521-5f94-4ede-bbc7-433f1c818e99");
		
		ftpDetails.setFileUrl("ftp://182.74.103.251/files/output.txt");
		ftpDetails.setPassword("test");
		ftpDetails.setUsername("test");

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
		jobDetails.setClinicians(null);

		when(jobDetailsService.getJobDetailsById(id)).thenReturn(jobDetails);
		jobDetailsRepository.deleteJobDetailById(id);
		fileDetailsRepository.deleteById(jobDetails.getInputFileDetails().getId());
		when(scheduler.deleteJob(new JobKey(id, Scheduler.DEFAULT_GROUP))).thenReturn(true);
		Assert.assertEquals(jobListService.deleteJobById(id), true);
	}
}
