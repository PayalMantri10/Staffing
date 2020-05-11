package com.envision.Staffing.controller;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
import org.springframework.http.ResponseEntity;
import static org.mockito.Mockito.when;

import com.envision.Staffing.controllers.TestQuartzController;
import com.envision.Staffing.model.ScheduleEmailRequest;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestQuartzControllerTest {

	ScheduleEmailRequest scheduleEmailRequest = new ScheduleEmailRequest();
	JobDetail jobDetail;
	Trigger trigger;
	Date date = new Date();
	LocalDateTime localDateTime = LocalDateTime.now().plusDays(2);
	ZoneId zoneid = ZoneId.of("Asia/Kolkata");

	@InjectMocks
	TestQuartzController testQuartzController;

	@Mock
	Scheduler scheduler;

	@Test
	public void scheduleEmailTest() throws SchedulerException {
		scheduleEmailRequest.setDateTime(localDateTime);
		scheduleEmailRequest.setTimeZone(zoneid);

		ZonedDateTime dateTime = ZonedDateTime.of(scheduleEmailRequest.getDateTime(),
				scheduleEmailRequest.getTimeZone());

		when(scheduler.scheduleJob(jobDetail, trigger)).thenReturn(date);
		testQuartzController.scheduleEmail(scheduleEmailRequest);
		Assert.assertEquals(testQuartzController.scheduleEmail(scheduleEmailRequest),
				ResponseEntity.ok("Email Scheduled Successfully!"));
	}
}
