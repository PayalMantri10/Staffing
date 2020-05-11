package com.envision.Staffing.controllers;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;

import com.envision.Staffing.model.Response;
import com.envision.Staffing.services.JobListService;

@Controller
@RequestMapping(path = "/jobList")
public class JobListController {

	@Autowired
	private JobListService jobListService;
	Logger log = Logger.getLogger(JobListController.class);

	@PostMapping(path = "/delete")
	@ResponseBody
	public Response deleteJob(@RequestBody String jobId) throws IOException {
		log.info("Entered with endpoint /delete in joblist");
		jobListService.deleteJobById(jobId);

		Response response = new Response();
		response.setMessage("Deleted");
		return response;
	}
}
