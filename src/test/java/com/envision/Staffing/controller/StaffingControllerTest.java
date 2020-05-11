package com.envision.Staffing.controller;

import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;

import com.envision.Staffing.controllers.StaffingController;
import com.envision.Staffing.model.Input;
import com.envision.Staffing.model.Output;
import com.envision.Staffing.services.ShiftPlanningService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class StaffingControllerTest {

	private InputStream is;
	Output output = new Output();
	Input input = new Input();

	@InjectMocks
	StaffingController staffingController;

	@Mock
	ShiftPlanningService shiftPlanningService;

	@Before
	public void init() {
		is = staffingController.getClass().getClassLoader().getResourceAsStream("excel.xlsx");
	}

	@Test
	public void indexTest() {
		Assert.assertEquals(staffingController.index(), "Greetings from Spring Boot!");
	}

	@Test
	public void getShiftPlanTest() throws IOException {
		when(shiftPlanningService.getShiftPlan(input)).thenReturn(output);
		Assert.assertEquals(staffingController.getShiftPlan(input), output);
	}

	@Test
	public void getShiftPlanFromFileTest() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "excel.xlsx", "multipart/form-data", is);
		String inputInJson = this.mapToJson(input);
		when(shiftPlanningService.processFileInput(file, inputInJson)).thenReturn(input);
		when(shiftPlanningService.getShiftPlan(input)).thenReturn(output);
		Assert.assertEquals(staffingController.getShiftPlanFromFile(file, inputInJson), output);
	}

	private String mapToJson(Object object) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(object);
	}
}
