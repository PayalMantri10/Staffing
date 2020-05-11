package com.envision.Staffing.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.envision.Staffing.model.Clinician;
import com.envision.Staffing.model.Day;
import com.envision.Staffing.model.Input;
import com.envision.Staffing.model.Output;
import com.envision.Staffing.model.Workload;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiftPlanningServiceTest {

	Output output = new Output();
	Input input = new Input();

	ShiftPlanningService shiftPlanningService = new ShiftPlanningService();
	ShiftCalculator shiftCalculator = new ShiftCalculator();
	Clinician[] clinicians;
	Workload workload;

	@Before
	public void initializeWorkloadAndCliniciansArray() {

		workload = new Workload();
		double workloadData[] = { 2.59, 2.58, 2.09, 2.09, 1.45, 0.75, 2.24, 3.68, 3.86, 3.78, 4.94, 5.64, 4.29, 4.5,
				6.23, 5.68, 4.75, 4.3, 4.44, 3.92, 4.12, 4.11, 3.99, 3.61, 3.85, 2.9, 2.34, 2.34, 1.62, 0.85, 2.51,
				4.13, 4.34, 4.26, 5.55, 6.33, 4.82, 5.06, 6.99, 6.39, 5.33, 4.83, 4.99, 4.4, 4.63, 4.61, 4.48, 4.05,
				4.45, 3.4, 2.75, 2.75, 1.9, 1, 2.94, 4.84, 5.08, 4.98, 6.51, 7.41, 5.64, 5.92, 8.18, 7.47, 6.24, 5.66,
				5.84, 5.15, 5.42, 5.41, 5.25, 4.75, 4.26, 2.97, 2.4, 2.4, 1.67, 0.87, 2.57, 4.24, 4.44, 4.36, 5.69,
				6.49, 4.93, 5.19, 7.16, 6.54, 5.46, 4.96, 5.1, 4.51, 4.74, 4.73, 4.59, 4.15, 4.05, 2.93, 2.37, 2.37,
				1.64, 0.86, 2.54, 4.18, 4.39, 4.31, 5.62, 6.41, 4.88, 5.12, 7.07, 6.47, 5.4, 4.89, 5.04, 4.45, 4.68,
				4.67, 4.53, 4.09, 2.9, 1.74, 1.41, 1.41, 0.98, 0.51, 1.5, 2.47, 2.59, 2.54, 3.32, 3.78, 2.88, 3.03,
				4.18, 3.81, 3.19, 2.88, 2.98, 2.63, 2.77, 2.76, 2.68, 2.42, 1.08, 0.35, 0.28, 0.28, 0.2, 0.1, 0.31,
				0.51, 0.53, 0.52, 0.68, 0.78, 0.58, 0.62, 0.85, 0.77, 0.65, 0.59, 0.61, 0.53, 0.56, 0.56, 0.55, 0.5 };
		workload.setFixedworkloadArray(workloadData);
		workload.setDayDuration(24);

		clinicians = new Clinician[3];
		for (int i = 0; i < 3; i++) {
			clinicians[i] = new Clinician();
		}

		int[] clinicianCountArray = new int[168];
		for (int i = 0; i < 168; i++) {
			clinicianCountArray[i] = i;
		}

		clinicians[0].setName("physician");
		clinicians[0].setClinicianCountPerHour(clinicianCountArray);
		clinicians[0].setCost(200);
		List<String> physicianExpressions = new ArrayList<>();
		physicianExpressions.add("0");
		clinicians[0].setExpressions(physicianExpressions);

		clinicians[1].setName("app");
		clinicians[1].setClinicianCountPerHour(clinicianCountArray);
		clinicians[1].setCost(65);
		List<String> appExpressions = new ArrayList<>();
		appExpressions.add("1");
		appExpressions.add("1 * physician");
		clinicians[1].setExpressions(appExpressions);

		clinicians[2].setName("scribe");
		clinicians[2].setClinicianCountPerHour(clinicianCountArray);
		clinicians[2].setCost(20);
		List<String> scribeExpressions = new ArrayList<>();
		scribeExpressions.add("2");
		scribeExpressions.add("1 * physician");
		scribeExpressions.add("1 * app");
		clinicians[2].setExpressions(scribeExpressions);

		Double physicianCapacity[] = { 1.0, 0.83, 0.67 };
		clinicians[0].setCapacity(physicianCapacity);
		Double appCapacity[] = { 0.5, 0.415, 0.335 };
		clinicians[1].setCapacity(appCapacity);
		Double scribeCapacity[] = { 0.30833333333333335, 0.2559166666666667, 0.20658333333333337 };
		clinicians[2].setCapacity(scribeCapacity);
	}

	@Test
	public void testGetShiftPlan() throws Exception {

		Integer[] shiftPreferences = new Integer[] { 12, 10, 8, 4 };
		input.setShiftLength(shiftPreferences);
		input.setClinician(clinicians);
		input.setLowerLimitFactor(0.85);
		input.setNotAllocatedEndTime(6);
		input.setNotAllocatedStartTime(1);
		input.setUpperLimitFactor(1.1);
		input.setPatientHourWait(2);
		input.setDayWorkload(null);

		shiftCalculator.setWorkloads(workload);
		output = shiftPlanningService.getShiftPlan(input);
		Assert.assertEquals(1, output.getHourlyDetail()[0].getNumberOfPhysicians(), 0);

	}

	@Test
	public void testAssignWorkLoad() throws Exception {

		Input inputs = new Input();
		Workload testingWorkload = new Workload();
		Workload workload = new Workload();

		Day[] day = new Day[2];
		for (int i = 0; i < 2; i++) {
			day[i] = new Day();
		}
		day[0].setName("Sunday");
		Double[] workloadForSunday = { 2.59, 2.58, 2.09, 2.09, 1.45, 0.75, 2.24, 3.68, 3.86, 3.78, 4.94, 5.64, 4.29,
				4.5, 6.23, 5.68, 4.75, 4.3, 4.44, 3.92, 4.12, 4.11, 3.99, 3.61 };
		day[0].setExpectedPatientsPerHour(workloadForSunday);
		day[1].setName("Monday");
		Double[] workloadForMonday = { 3.85, 2.9, 2.34, 2.34, 1.62, 0.85, 2.51, 4.13, 4.34, 4.26, 5.55, 6.33, 4.82,
				5.06, 6.99, 6.39, 5.33, 4.83, 4.99, 4.4, 4.63, 4.61, 4.48, 4.05 };
		day[1].setExpectedPatientsPerHour(workloadForMonday);
		inputs.setDayWorkload(day);

		testingWorkload = shiftPlanningService.assignWorkload(inputs, workload);
		Assert.assertEquals(2.09, testingWorkload.getFixedworkloadArray()[2], 0.001);

	}

}