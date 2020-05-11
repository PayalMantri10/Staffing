package com.envision.Staffing.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.envision.Staffing.controllers.JobDetailsController;
import com.envision.Staffing.model.Clinician;
import com.envision.Staffing.model.JobDetails;
import com.envision.Staffing.model.Response;
import com.envision.Staffing.services.JobDetailsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.eq;

@RunWith(MockitoJUnitRunner.class)
public class JobDetailsControllerTest {

	JobDetails mockJobDetails = new JobDetails();
	Response response = new Response();
	List<Clinician> clinicians = new LinkedList<>();
	Clinician physician = new Clinician();
	Clinician app = new Clinician();
	Clinician scribe = new Clinician();
	List<JobDetails> jobList = new LinkedList<>();

	@InjectMocks
	JobDetailsController jobDetailsController;

	@Mock
	JobDetailsService jobDetailsService;

	@Test
	public void addNewJobDetailsTest() throws IOException {

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

		mockJobDetails.setId("2ac0ed25-ed5e-4891-85e1-12acac13d3e6");
		mockJobDetails.setName("Test1");
		mockJobDetails.setCronExpression(null);
		mockJobDetails.setInputFileDetails(null);
		mockJobDetails.setInputFormat(null);
		mockJobDetails.setInputFtpDetails(null);
		mockJobDetails.setLowerUtilizationFactor(null);
		mockJobDetails.setOutputEmailId(null);
		mockJobDetails.setOutputFormat(null);
		mockJobDetails.setOutputFtpDetails(null);
		mockJobDetails.setShiftLengthPreferences(null);
		mockJobDetails.setStatus("DRAFT");
		mockJobDetails.setUpperUtilizationFactor(null);
		mockJobDetails.setClinicians(clinicians);

		String inputInJson = this.mapToJson(mockJobDetails);
		response.setMessage("Successfully saved Test1 as draft");
		when(jobDetailsService.createOrUpdateJobDetails(any(JobDetails.class), eq(null))).thenReturn(mockJobDetails);
		Response response1 = jobDetailsController.addNewJobDetails(null, inputInJson);
		Assert.assertEquals(response1.getMessage(), response.getMessage());
	}

	@Test
	public void getAllJobDetailsTest() {
		when(jobDetailsService.getAllJobDetails()).thenReturn(jobList);
		Assert.assertEquals(jobDetailsController.getAllJobDetails(), jobList);
	}

	@Test
	public void getJobByIdTest() {
		String id = "2ac0ed25-ed5e-4891-85e1-12acac13d3e6";
		when(jobDetailsService.getJobDetailsById(id)).thenReturn(mockJobDetails);
		Assert.assertEquals(jobDetailsController.getJobById(id), mockJobDetails);
	}

	private String mapToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(object);
	}

}
