package com.envision.Staffing.controller;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.envision.Staffing.controllers.JobListController;
import com.envision.Staffing.model.Clinician;
import com.envision.Staffing.model.JobDetails;
import com.envision.Staffing.model.Response;
import com.envision.Staffing.services.JobListService;

@RunWith(MockitoJUnitRunner.class)
public class JobListControllerTest {

	JobDetails mockJobDetails = new JobDetails();
	Response response = new Response();
	List<Clinician> clinicians = new LinkedList<>();
	Clinician physician = new Clinician();
	Clinician app = new Clinician();
	Clinician scribe = new Clinician();

	@Mock
	JobListService jobListService;

	@InjectMocks
	JobListController jobListController;

	@Test
	public void deleteJobTest() throws IOException {

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

		String id = "2ac0ed25-ed5e-4891-85e1-12acac13d3e6";
		when(jobListService.deleteJobById(id)).thenReturn(true);
		response.setMessage("Deleted");
		Response response1 = jobListController.deleteJob(id);
		Assert.assertEquals(response1.getMessage(), response.getMessage());
	}
}
