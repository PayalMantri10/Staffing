package com.envision.Staffing.controllers;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.envision.Staffing.job.EmailJob;
import com.envision.Staffing.model.ScheduleEmailRequest;

@RestController
public class TestQuartzController {

	@Autowired
	private Scheduler scheduler;

	Logger log = Logger.getLogger(TestQuartzController.class);

	@PostMapping("/scheduleEmail")
	public ResponseEntity<String> scheduleEmail(@Valid @RequestBody ScheduleEmailRequest scheduleEmailRequest) {
		try {
			ZonedDateTime dateTime = ZonedDateTime.of(scheduleEmailRequest.getDateTime(),
					scheduleEmailRequest.getTimeZone());
			if (dateTime.isBefore(ZonedDateTime.now())) {
				return ResponseEntity.badRequest().body("dateTime must be after current time");
			}
			JobDetail jobDetail = buildJobDetail(scheduleEmailRequest);
			Trigger trigger = buildJobTrigger(jobDetail, dateTime);
			scheduler.scheduleJob(jobDetail, trigger);
			return ResponseEntity.ok("Email Scheduled Successfully!");
		} catch (SchedulerException ex) {
			log.error("Error happened in scheduling email :", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error scheduling email. Please try later!");
		}
	}

	private JobDetail buildJobDetail(ScheduleEmailRequest scheduleEmailRequest) {
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("email", scheduleEmailRequest.getEmail());
		jobDataMap.put("subject", scheduleEmailRequest.getSubject());
		jobDataMap.put("body", scheduleEmailRequest.getBody());

		return JobBuilder.newJob(EmailJob.class).withIdentity(UUID.randomUUID().toString(), "email-jobs")
				.withDescription("Send Email Job").usingJobData(jobDataMap).storeDurably().build();
	}

	private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {

		return TriggerBuilder.newTrigger().forJob(jobDetail)
				.withIdentity(jobDetail.getKey().getName(), "email-triggers").withDescription("Send Email Trigger")
				.startAt(Date.from(startAt.toInstant())).withSchedule(CronScheduleBuilder.cronSchedule("0 0/5 * * * ?"))
				.build();
	}
}
