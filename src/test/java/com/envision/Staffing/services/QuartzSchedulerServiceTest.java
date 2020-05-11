package com.envision.Staffing.services;

import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.envision.Staffing.model.JobDetails;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QuartzSchedulerServiceTest {

	JobDetails jobDetails = new JobDetails();
	JobDetail jobDetail;
	Trigger trigger;
	TriggerKey triggerkey;
	Date date = new Date();

	@InjectMocks
	QuartzSchedulerService quartzSchedulerService;

	@Mock
	Scheduler scheduler;

	@Test
	public void jobTest() throws SchedulerException {
		jobDetails.setId("2ac0ed25-ed5e-4891-85e1-12acac13d3e6");
		jobDetails.setName("Test1");
		jobDetails.setCronExpression("0 0/1 * 1/1 * ? *");
		jobDetails.setInputFileDetails(null);
		jobDetails.setInputFormat("DATA_FILE");
		jobDetails.setInputFtpDetails(null);
		jobDetails.setLowerUtilizationFactor((float) 0.85);
		jobDetails.setOutputEmailId("a@gmail.com");
		jobDetails.setOutputFormat("EMAIL");
		jobDetails.setOutputFtpDetails(null);
		jobDetails.setShiftLengthPreferences(null);
		jobDetails.setStatus("SCHEDULED");
		jobDetails.setUpperUtilizationFactor((float) 1.10);
		jobDetails.setClinicians(null);

		quartzSchedulerService.scheduleJob(jobDetails);
		when(scheduler.scheduleJob(jobDetail, trigger)).thenReturn(date);

		quartzSchedulerService.rescheduleJob(jobDetails.getId(), jobDetails);
		when(scheduler.rescheduleJob(triggerkey, trigger)).thenReturn(date);
	}
}
