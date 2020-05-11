package com.envision.Staffing.controllers;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.envision.Staffing.model.JobDetails;
import com.envision.Staffing.model.Response;
import com.envision.Staffing.services.JobDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(path = "/jobDetails")
public class JobDetailsController {

	@Autowired
	private JobDetailsService jobDetailsService;
	Logger log = Logger.getLogger(JobDetailsController.class);

	@RequestMapping(path = "/add", method = RequestMethod.POST, consumes = { "multipart/form-data" })
	@ResponseBody
	public Response addNewJobDetails(@RequestPart(required = false) MultipartFile file,
			@RequestPart("input") String input) throws IOException {
		log.info("called the endpoint /add in jobdetails to create or update the job details ");
		JobDetails jobDetails = new ObjectMapper().readValue(input, JobDetails.class);
		if (file != null && jobDetails != null)
			jobDetailsService.createOrUpdateJobDetails(jobDetails, file.getBytes());
		else
			jobDetailsService.createOrUpdateJobDetails(jobDetails, null);

		Response response = new Response();
		if (jobDetails.getStatus().contentEquals("SCHEDULED")) {
			log.info("Successfully scheduled Job :" + jobDetails.getName());
			response.setMessage("Successfully schedules Job: " + jobDetails.getName());
		} else {
			if (jobDetails.getName().contentEquals("")) {
				log.info("Successfully saved the job as draft");
				response.setMessage("Successfully saved job as draft");
			} else {
				log.info("Successfully saved the Job :" + jobDetails.getName() + " as draft");
				response.setMessage("Successfully saved " + jobDetails.getName() + " as draft");
			}
		}
		return response;
	}

	@GetMapping(path = "/all")
	public @ResponseBody Iterable<JobDetails> getAllJobDetails() {
		log.info("called the endpoint /all in jobdetails to get all the job details ");
		return jobDetailsService.getAllJobDetails();
	}

	@GetMapping(path = "/get")
	public @ResponseBody JobDetails getJobById(@RequestParam("jobId") String jobId) {
		log.info("called the endpoint /get in jobdetails to get job details with given id ");
		return jobDetailsService.getJobDetailsById(jobId);
	}

}
