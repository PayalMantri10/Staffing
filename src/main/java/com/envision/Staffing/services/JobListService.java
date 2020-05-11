package com.envision.Staffing.services;

import org.apache.log4j.Logger;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.envision.Staffing.model.JobDetails;
import com.envision.Staffing.repository.FileDetailsRepository;
import com.envision.Staffing.repository.FtpDetailsRepository;
import com.envision.Staffing.repository.JobDetailsRepository;

@Service
public class JobListService {

	@Autowired
	private FileDetailsRepository fileDetailsRepository;
	@Autowired

	private FtpDetailsRepository ftpDetailsRepository;
	@Autowired

	private JobDetailsRepository jobDetailsRepository;

	@Autowired
	private JobDetailsService jobDetailsService;

	@Autowired
	private Scheduler scheduler;

	Logger log = Logger.getLogger(JobListService.class);

	public boolean deleteJobById(String jobId) {
		boolean flag;
		log.info("Entering method for deleting job by using id :");
		try {
			JobDetails jobDetails = jobDetailsService.getJobDetailsById(jobId);

			jobDetailsRepository.deleteJobDetailById(jobId); // deletes job and clinicians
			if (jobDetails.getInputFormat().contentEquals("FTP_URL")) {
				log.info("if jobDetail's input format is ftp_url, Deletes input_ftp ");
				ftpDetailsRepository.deleteById(jobDetails.getInputFtpDetails().getId()); // deletes input_ftp
			} else if (jobDetails.getInputFormat().contentEquals("DATA_FILE")) {
				log.info("if jobDetail's input format is data_file, Deletes input_file ");
				fileDetailsRepository.deleteById(jobDetails.getInputFileDetails().getId()); // deletes input_file
			}
			if (jobDetails.getOutputFormat().contentEquals("FTP_URL")) {
				log.info("if jobDetail's output format is ftp_url, Deletes output_ftp ");
				ftpDetailsRepository.deleteById(jobDetails.getOutputFtpDetails().getId()); // deletes output_ftp
			}
			if (jobDetails.getStatus().contentEquals("SCHEDULED")) {
				log.info("if jobDetail's status is scheduled, Delete jobs ");
				scheduler.deleteJob(new JobKey(jobId, Scheduler.DEFAULT_GROUP)); // deletes related jobs
			}
			flag = true;

		} catch (Exception ex) {
			log.error("Error happened in deleting the job by id :", ex);
			flag = false;
			ex.printStackTrace();
		}
		return flag;
	}

}
