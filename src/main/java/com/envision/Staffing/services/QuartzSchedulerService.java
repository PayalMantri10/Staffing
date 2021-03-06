package com.envision.Staffing.services;

import java.util.Date;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.envision.Staffing.job.AutorunJob;
import com.envision.Staffing.model.JobDetails;

@Service
public class QuartzSchedulerService {

	@Autowired
	private Scheduler scheduler;

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public void scheduleJob(JobDetails jobDetails) {
		JobDetail jobDetail = buildJobDetail(jobDetails);
		Trigger trigger = buildJobTrigger(jobDetail, jobDetails);
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private JobDetail buildJobDetail(JobDetails jobDetails) {
		JobDataMap jobDataMap = new JobDataMap();

		jobDataMap.put("jobId", jobDetails.getId());

		return JobBuilder.newJob(AutorunJob.class) // TODO: Change to respective class when added
				.withIdentity(jobDetails.getId()).withDescription(jobDetails.getName()).usingJobData(jobDataMap)
				.storeDurably() // Whether or not the Job should remain stored after it is orphaned (no Triggers
								// point to it).
				.build();
	}

	private Trigger buildJobTrigger(JobDetail jobDetail, JobDetails jobDetails) {
		return TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(jobDetail.getKey().getName())
				.withDescription(jobDetail.getDescription()).startAt(new Date())
				.withSchedule(CronScheduleBuilder.cronSchedule(jobDetails.getCronExpression())).build();
	}
}
