package com.envision.Staffing.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.envision.Staffing.model.Clinician;
import com.envision.Staffing.model.HourlyDetail;
import com.envision.Staffing.model.Shift;
import com.envision.Staffing.model.Workload;
import com.envision.Staffing.services.ShiftCalculator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ShiftCalculatorTest {

	@Autowired
	ShiftCalculator shiftCalculator;

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
	public void testGetClinicianCountByName() throws Throwable {

		ShiftCalculator shiftCalculator = new ShiftCalculator();

		String name = "physician";
		int hour = 11;
		int result = shiftCalculator.getClinicianCountByName(name, clinicians, hour);
		Assert.assertEquals(11, result, 0);

		result = shiftCalculator.getClinicianCountByName(name, clinicians, 145);
		Assert.assertEquals(145, result, 0);

		name = "app";
		result = shiftCalculator.getClinicianCountByName(name, clinicians, 65);
		Assert.assertEquals(65, result, 0);

		result = shiftCalculator.getClinicianCountByName(name, clinicians, 167);
		Assert.assertEquals(167, result, 0);

		name = "Scribe";
		result = shiftCalculator.getClinicianCountByName(name, clinicians, 0);
		Assert.assertEquals(0, result, 0);

		result = shiftCalculator.getClinicianCountByName(name, clinicians, 99);
		Assert.assertEquals(99, result, 0);

	}

	@Test
	public void testMin() throws Throwable {
		double a = 0.0;
		double b = 1.2;
		double result = shiftCalculator.min(a, b);
		Assert.assertEquals(0.0, result, 0.001);

		result = shiftCalculator.min(2.2, 2.1);
		Assert.assertEquals(2.1, result, 0.01);

		result = shiftCalculator.min(5, 5.1);
		Assert.assertEquals(5.0, result, 0.01);

		result = shiftCalculator.min(10, 20);
		Assert.assertEquals(10, result, 0.01);

	}

	@Test
	public void testRound() throws Throwable {

		double a = 0.335;
		int b = 2;

		double result = shiftCalculator.round(a, b);
		Assert.assertEquals(0.34, result, 0.001);

		result = shiftCalculator.round(0.5465833333333334, 2);
		Assert.assertEquals(0.55, result, 0.01);

		result = shiftCalculator.round(0.885, 2);
		Assert.assertEquals(0.89, result, 0.01);

	}

	@Test
	public void testEvaluate() throws Throwable {

		String expression = "1 * physician";
		int hour = 2;
		double result = shiftCalculator.evaluate(expression, clinicians, hour);
		Assert.assertEquals(2, result, 0.001);

		expression = "2 * physician";
		hour = 100;
		result = shiftCalculator.evaluate(expression, clinicians, hour);
		Assert.assertEquals(200, result, 0.001);

		expression = "1 * app";
		hour = 67;
		result = shiftCalculator.evaluate(expression, clinicians, hour);
		Assert.assertEquals(67, result, 0.001);

		expression = "2 * app";
		hour = 43;
		result = shiftCalculator.evaluate(expression, clinicians, hour);
		Assert.assertEquals(86, result, 0.001);
	}

	@Test
	public void testIsConditionSatisfied() throws Throwable {

		int start = 0;
		int shiftLength = 4;
		int index = 1;

		boolean result = shiftCalculator.isConditionStatisfied(clinicians, start, shiftLength, index);
		Assert.assertEquals(false, result);
	}

	@Test
	public void testIsConditionSatisfied_Phyiscian() throws Throwable {

		int start = 0;
		int shiftLength = 4;
		int index = 0;

		boolean result = shiftCalculator.isConditionStatisfied(clinicians, start, shiftLength, index);
		Assert.assertEquals(true, result);
	}

	@Test
	public void testGetClinicianWithLeastCost() throws Throwable {
		int index = 2;
		Clinician result = shiftCalculator.getClinicianWithLeastCost(index, clinicians);
		Assert.assertEquals("scribe", result.getName());
	}

	@Test
	public void testCalculatePhysicianSlotsForAll() throws Throwable {
		int shiftLength = 4;
		double lowerLimitFactor = 0.85;

		workload.setSizeOfArray(12);
		shiftCalculator.setWorkloads(workload);

		shiftCalculator.calculatePhysicianSlotsForAll(22, 4, shiftLength, clinicians, lowerLimitFactor);
		Assert.assertEquals(0, clinicians[0].getClinicianCountPerHour()[0], 0);

		shiftCalculator.calculatePhysicianSlotsForAll(1, 6, shiftLength, clinicians, lowerLimitFactor);
		Assert.assertEquals(6, clinicians[0].getClinicianCountPerHour()[0], 0);

	}

	@Test
	public void testCalculateLastHourSlots() throws Throwable {
		int shiftLength = 4;
		double upperLimitFactor = 1.1;
		int notAllocatedStartTime = 22;
		int notAllocatedEndTime = 4;

		workload.setSizeOfArray(12);
		shiftCalculator.setWorkloads(workload);
		shiftCalculator.calculateLastHourSlots(upperLimitFactor, notAllocatedStartTime, notAllocatedEndTime, clinicians,
				shiftLength);
		Assert.assertEquals(0, clinicians[0].getClinicianCountPerHour()[0], 0);

		notAllocatedStartTime = 1;
		notAllocatedEndTime = 6;
		shiftCalculator.calculateLastHourSlots(upperLimitFactor, notAllocatedStartTime, notAllocatedEndTime, clinicians,
				shiftLength);
		Assert.assertEquals(6, clinicians[0].getClinicianCountPerHour()[0], 0);

	}

	@Test
	public void testCheckIfPhysicianToBeAdded() throws Throwable {

		int start = 0;
		int shiftLength = 12;
		double lowerLimitFactor = 0.85;
		Double capacity[] = { 0.5, 0.415, 0.335 };

		clinicians[0].setCapacity(capacity);
		shiftCalculator.setWorkloads(workload);

		int result = shiftCalculator.checkIfPhysicianToBeAdded(shiftLength, start, lowerLimitFactor,
				clinicians[0].getCapacity());
		Assert.assertEquals(1, result, 0);
	}

	@Test
	public void testCheckAndAddClinicianForAllShift() throws Throwable {
		int start = 0;
		int shiftLength = 4;
		double lowerLimitFactor = 0.85;
		int result;

		workload.setSizeOfArray(12);
		shiftCalculator.setWorkloads(workload);
		result = shiftCalculator.CheckAndAddClinicianForAllShift(shiftLength, clinicians, start, lowerLimitFactor);
		Assert.assertEquals(1, result, 0);
	}

	@Test
	public void testCheckandAddForLastShift() throws Throwable {
		int start = 1;
		int shiftLength = 4;
		double upperLimitFactor = 1.1;
		int result;

		workload.setSizeOfArray(12);
		shiftCalculator.setWorkloads(workload);
		result = shiftCalculator.CheckandAddForLastShift(upperLimitFactor, start, clinicians, shiftLength);
		Assert.assertEquals(1, result, 0);
	}

	@Test
	public void testEvaluateFunction() throws Throwable {
		int nextHourClinicianCount = 12;
		int currentCount = 10;
		Boolean result = shiftCalculator.evaluateFunction(nextHourClinicianCount, currentCount, ">");
		Assert.assertEquals(true, result);
	}

	@Test
	public void testGetNewShift() throws Throwable {

		int start = 0;
		int shiftLength = 12;
		shiftCalculator.setWorkloads(workload);

		Shift newShift = new Shift();
		newShift.setStartTime(start % workload.getDayDuration());
		newShift.setEndTime((start + shiftLength) % workload.getDayDuration());
		newShift.setDay(start / workload.getDayDuration());
		newShift.setNoOfHours(shiftLength);
		newShift.setPhysicianType(clinicians[0].getName());

		Shift shift = shiftCalculator.getNewShift(shiftLength, start, clinicians[0].getName());

		Assert.assertEquals(shift.hashCode(), newShift.hashCode());
		Assert.assertEquals(newShift.getDay(), shift.getDay());
		Assert.assertEquals(newShift.getEndTime(), shift.getEndTime());
	}

	@Test
	public void testGenerateHourlyDetail() throws Throwable {
		int patientHourWait = 1;
		double lowerLimitFactor = 0.85;
		workload.setDocEfficency(1.2);
		shiftCalculator.setWorkloads(workload);
		HourlyDetail[] hourlyDetailList = shiftCalculator.generateHourlyDetail(patientHourWait, clinicians,
				workload.getDocEfficency(), lowerLimitFactor);
		patientHourWait = 2;
		hourlyDetailList = shiftCalculator.generateHourlyDetail(patientHourWait, clinicians, workload.getDocEfficency(),
				lowerLimitFactor);

		Assert.assertEquals(11, hourlyDetailList[11].getNumberOfPhysicians(), 0);
		Assert.assertEquals(133, hourlyDetailList[133].getNumberOfPhysicians(), 0);
		Assert.assertEquals(2.59, hourlyDetailList[0].getExpectedWorkLoad(), 0.001);

	}

	@Test
	public void testCalculateNewWorkloads() throws Throwable {
		int start = 0;
		int shiftLength = 4;
		shiftCalculator.setWorkloads(workload);
		Double clinicianCapacity[] = { 0.5, 0.415, 0.335 };

		shiftCalculator.calculateNewWorkloads(start, start + shiftLength, clinicianCapacity);
		Assert.assertEquals(-0.41, workload.getWorkloadArray()[1], 0.001);
	}

	@Test
	public void testCalculateCapacities() throws Throwable {
		int start = 0;
		int shiftLength = 4;
		shiftCalculator.setWorkloads(workload);
		Double clinicianCapacity[] = { 0.5, 0.415, 0.335 };

		shiftCalculator.calculateCapacities(start, start + shiftLength, clinicianCapacity);
		Assert.assertEquals(0.42, workload.getCapacityArray()[1], 0.001);
	}

}
