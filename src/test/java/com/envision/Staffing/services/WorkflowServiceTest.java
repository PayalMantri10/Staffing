package com.envision.Staffing.services;

import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.envision.Staffing.model.FileDetails;
import com.envision.Staffing.model.JobDetails;

@RunWith(MockitoJUnitRunner.Silent.class)
public class WorkflowServiceTest {

	JobDetails jobDetails = new JobDetails();
	FileDetails fileDetails = new FileDetails();

	@InjectMocks
	WorkflowService workflowService;

	@Mock
	JobDetailsService jobDetailsService;

	@Mock
	ShiftPlanningService shiftPlannerSerivce;

	@Mock
	EmailService emailService;

	@Test
	public void autorunWorkflowServiceTest() throws Exception {
		String id = "2ac0ed25-ed5e-4891-85e1-12acac13d3e6";
		byte[] file = "hello".getBytes();

		fileDetails.setDataFile(file);
		fileDetails.setFileExtension("xlsx");
		fileDetails.setId("ce362521-5f94-4ede-bbc7-433f1c818e99");

		jobDetails.setId(id);
		jobDetails.setName("Test1");
		jobDetails.setCronExpression("0 0/1 * 1/1 * ? *");
		jobDetails.setInputFileDetails(fileDetails);
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

		when(jobDetailsService.getJobDetailsById(id)).thenReturn(jobDetails);
		workflowService.autorunWorkflowService(id);
	}

}
