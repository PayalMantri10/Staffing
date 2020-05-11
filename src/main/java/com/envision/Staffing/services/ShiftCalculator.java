package com.envision.Staffing.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.envision.Staffing.model.Clinician;
import com.envision.Staffing.model.HourlyDetail;
import com.envision.Staffing.model.Shift;
import com.envision.Staffing.model.Workload;

enum allocation {
	AllocationForNormalShift, AllocationForRestrictionShift, SkipAllocation
};

@Service
public class ShiftCalculator {
	Logger log = Logger.getLogger(ShiftCalculator.class);
	Workload wl;

	public void setWorkloads(Workload w) {
		// Initialize workload array
		wl = w;
	}

	public void calculatePhysicianSlotsForAll(int notAllocatedStartTime, int notAllocatedEndTime, int shiftLength,
			Clinician[] clinicians, double lowerLimitFactor) {
		// Check every 12 hour slot Eg : 0-12 , 1-13, 2-14 ..... (Assuming numberOfHours
		// = 12)
		log.info("Calculating Clinician Slots for every hour with the shiftLength " + shiftLength);
		int start = 0, previousstart = 0, holdingPreviousstart = 0;
		double factor = lowerLimitFactor;
		while (start + shiftLength < wl.getSizeOfArray()) {
			String b1 = "AllocationForNormalShift";
			if ((notAllocatedStartTime <= notAllocatedEndTime) && start % 24 >= notAllocatedStartTime
					&& start % 24 <= notAllocatedEndTime) {

				previousstart = ((start / 24) * 24) + notAllocatedStartTime - 1;
				if (previousstart < 0) {
					start += 1;
					b1 = "SkipAllocation";
				} else {
					calculatePhysicianSlotForOnce(previousstart, shiftLength, clinicians, lowerLimitFactor);
					start += 1;
					b1 = "AllocationForRestrictionShift";
				}
			}
			if ((notAllocatedStartTime > notAllocatedEndTime)
					&& ((start % 24 >= notAllocatedStartTime) || (start % 24 <= notAllocatedEndTime))) {

				if (start < 24 && allocation.valueOf(b1).ordinal() == 0 && start % 24 <= notAllocatedEndTime) {
					start += 1;
					b1 = "SkipAllocation";
				} else if ((start % 24 >= notAllocatedStartTime)) {
					previousstart = ((start / 24) * 24) + notAllocatedStartTime - 1;
					holdingPreviousstart = previousstart;
					b1 = "AllocationForRestrictionShift";
				} else {
					previousstart = holdingPreviousstart;
					b1 = "AllocationForRestrictionShift";
				}
				if (allocation.valueOf(b1).ordinal() == 1) {
					calculatePhysicianSlotForOnce(previousstart, shiftLength, clinicians, lowerLimitFactor);
					start += 1;
				}
			}
			if (allocation.valueOf(b1).ordinal() == 0) {
				start = CheckAndAddClinicianForAllShift(shiftLength, clinicians, start, factor);

			}
		}

	}

	int CheckAndAddClinicianForAllShift(int shiftLength, Clinician[] clinicians, int start, double factor) {
		int index = clinicians.length - 1;
		int flag = 1;
		int isShiftToNextHour = 0;
		boolean conditionalValue = false;
		boolean[] array = { true, true, true };
		boolean shiftNextHour = true;

		for (; index >= 0 && isShiftToNextHour == 0; index--) {
			Clinician clinician = getClinicianWithLeastCost(index, clinicians);
			conditionalValue = isConditionStatisfied(clinicians, start, shiftLength, index);// checking
			do {

				Shift newShift = getNewShift(shiftLength, start, clinician.getName());
				flag = checkIfPhysicianToBeAdded(shiftLength, start, factor, clinician.getCapacity());// checking
																										// utilization

				if (flag == 1 && conditionalValue) // adding shift
				{
					addNewShift(start, shiftLength, newShift, clinician.getCapacity(),
							clinician.getClinicianCountPerHour());
					index = 3;
					break;
				} else if (flag == 0 && index == clinicians.length - 1) // even scribe cannot be added,move to
																		// next hour
				{
					start = start + 1;
					isShiftToNextHour = 1;
					break;
				} else {
					array[index] = false;
				}
				conditionalValue = isConditionStatisfied(clinicians, start, shiftLength, index);
			} while (conditionalValue && flag == 1);
		}

		for (boolean value : array) {
			if (value)
				shiftNextHour = false;
		}

		if (shiftNextHour) {
			shiftNextHour = false;
			start = start + 1;

		}
		return start;
	}

	public void calculatePhysicianSlotForOnce(int start, int shiftLength, Clinician[] clinicians,
			double lowerLimitFactor) {
		// function - only one shift for previous start
		double factor = lowerLimitFactor;
		int j = start + shiftLength;
		while (j == (start + shiftLength) && (start + shiftLength) < wl.getSizeOfArray()) {
			start = CheckAndAddClinicianForAllShift(shiftLength, clinicians, start, factor);
		}
	}

	public double evaluate(String expression, Clinician[] clinicians, int hour) {

		String[] elements = expression.split(" ");
		int value = getClinicianCountByName(elements[2], clinicians, hour);
		switch (elements[1]) {
		case "+":
			return Double.parseDouble(elements[0]) + value;
		case "*":
			return Double.parseDouble(elements[0]) * value;
		default:
			return 0;
		}

	}

	// function to return the count per hour of the clinician specified by the name
	public int getClinicianCountByName(String name, Clinician[] clinicians, int hour) {
		if (name != null) {
			Clinician clinician = Arrays.stream(clinicians).filter(p -> name.equalsIgnoreCase(p.getName())).findFirst()
					.get();
			if (clinician != null) {
				return clinician.getClinicianCountPerHour()[hour];
			}

		}

		return 0;
	}

	public boolean isConditionStatisfied(Clinician[] clinicians, int start, int shiftLength, int index) {
		// if clinician is more important, no need to check for any conditions
		if (clinicians[index].getName() != null) {

			if (clinicians[index].getExpressions().size() == 1)
				return true;
			else {
				for (int hour = start; hour < start + shiftLength && hour < 168; hour++) {
					double value = 0.0d;
					for (int j = 1; j < clinicians[index].getExpressions().size(); j++) {
						value += evaluate(clinicians[index].getExpressions().get(j), clinicians, hour);
					}
					if (evaluateFunction(clinicians[index].getClinicianCountPerHour()[hour] + 1, value, ">"))
						return false;
				}
				return true;

			}
		}
		return false;
	}

	public boolean evaluateFunction(double lefthandValue, double rightHandValue, String operator) {
		switch (operator) {

		case ">":
			return lefthandValue > rightHandValue;
		case ">=":
			return lefthandValue >= rightHandValue;
		case "<=":
			return lefthandValue <= rightHandValue;
		case "==":
			return lefthandValue == rightHandValue;
		case "!=":
			return lefthandValue != rightHandValue;

		case "<":
		default:
			return lefthandValue < rightHandValue;

		}
	}

	int checkIfPhysicianToBeAdded(int numberOfHours, int start, double factor, Double[] physicianCapacity) {
		int flag = 1;
		double capacityOfCurrentDoctor = 0;
		for (int j = start; j < start + numberOfHours; j++) {
			double value = wl.getFixedworkloadArray()[j] - wl.getCapacityArray()[j];
			if (value <= 0) {
				value = 0;
				flag = 0;
				break;
			}
			if ((j - start) % wl.getDayDuration() == 0) {
				capacityOfCurrentDoctor = round(capacityOfCurrentDoctor + min(physicianCapacity[0], value), 2);

			} else if ((j - start) % wl.getDayDuration() == numberOfHours - 1) {
				capacityOfCurrentDoctor = round(capacityOfCurrentDoctor + min(physicianCapacity[2], value), 2);

			} else {
				capacityOfCurrentDoctor = round(capacityOfCurrentDoctor + min(physicianCapacity[1], value), 2);

			}
			if ((j - start) == (numberOfHours - 1)
					&& ((capacityOfCurrentDoctor / (numberOfHours * physicianCapacity[1])) < factor)) {

				flag = 0;
				break;
			}
		}
		return flag;
	}

	public void calculateLastHourSlots(double upperLimitFactor, int notAllocatedStartTime, int notAllocatedEndTime,
			Clinician[] clinicians, int sizeOfSlot) {
		log.info("Calculating Clinician Slots for every hour with the shiftLength " + sizeOfSlot);
		int start = 0, previousstart = 0, StoredPreviousstart = 0;
		// A four - hour slot is added whenever there are 2 consecutive slots where
		// utilization > given range (110%)
		while (start < wl.getSizeOfArray()) {

			String b1 = "AllocationForNormalShift";
			if ((notAllocatedStartTime <= notAllocatedEndTime) && start % 24 >= notAllocatedStartTime
					&& start % 24 <= notAllocatedEndTime) {

				previousstart = ((start / 24) * 24) + notAllocatedStartTime - 1;
				if (previousstart < 0) {
					start += 1;
					b1 = "SkipAllocation";
				} else {
					calculateLastHourSlotForOnce(upperLimitFactor, previousstart, clinicians, sizeOfSlot);
					start += 1;
					b1 = "AllocationForRestrictionShift";

				}
			}
			if (notAllocatedStartTime > notAllocatedEndTime
					&& ((start % 24 >= notAllocatedStartTime) || (start % 24 <= notAllocatedEndTime))) {
				if ((start % 24 >= notAllocatedStartTime)) {
					previousstart = ((start / 24) * 24) + notAllocatedStartTime - 1;
					StoredPreviousstart = previousstart;
					b1 = "AllocationForRestrictionShift";
				} else {
					if (start < 24 && allocation.valueOf(b1).ordinal() == 0) {
						start += 1;
						b1 = "SkipAllocation";
					} else {
						previousstart = StoredPreviousstart;
						b1 = "AllocationForRestrictionShift";
					}
				}
				if (allocation.valueOf(b1).ordinal() == 1) {
					calculateLastHourSlotForOnce(upperLimitFactor, previousstart, clinicians, sizeOfSlot);
					start += 1;
				}
			}

			if (allocation.valueOf(b1).ordinal() == 0) {
				start = CheckandAddForLastShift(upperLimitFactor, start, clinicians, sizeOfSlot);
			}
		}
	}

	public void calculateLastHourSlotForOnce(double upperLimitFactor, int start, Clinician[] clinicians,
			int sizeOfSlot) {
		// function - only one shift for going back to previous start and assigning a
		// shift
		int j = start;
		while (start == j) {
			start = CheckandAddForLastShift(upperLimitFactor, start, clinicians, sizeOfSlot);
		}
	}

	int CheckandAddForLastShift(double upperLimitFactor, int start, Clinician[] clinicians, int sizeOfSlot) {
		int j = start;

		// checking for utilization < 1.1 to not add clinicians
		if (wl.getFixedworkloadArray()[j] / wl.getCapacityArray()[j] < upperLimitFactor) {
			start = start + 1;

		} else {
			boolean conditionalValue = true;
			for (int i = clinicians.length - 1; i >= 0; i--) {
				Clinician clinician = getClinicianWithLeastCost(i, clinicians);
				int tempSlotSize = sizeOfSlot;
				conditionalValue = isConditionStatisfied(clinicians, start, tempSlotSize, i);

				while (conditionalValue) {
					Shift newShift = getNewShift(sizeOfSlot, start, clinician.getName());

					addNewShift(start, sizeOfSlot, newShift, clinician.getCapacity(),
							clinician.getClinicianCountPerHour());
					i = 3;
					break;
				}

				if (wl.getFixedworkloadArray()[j] / wl.getCapacityArray()[j] < upperLimitFactor)
					break;
			}
		}
		return start;
	}

	public Clinician getClinicianWithLeastCost(int index, Clinician[] clinicians) {
		Clinician[] tempArray = Arrays.copyOf(clinicians, clinicians.length);
		Comparator<Clinician> comparator = Comparator.comparing(Clinician::getCost);
		Arrays.sort(tempArray, comparator);
		return tempArray[clinicians.length - index - 1];
	}

	private void addNewShift(int start, int shiftLength, Shift newShift, Double[] capacity, int[] clinicianCounter) {
		// A 12 hour slot can be added
		if (start + shiftLength > wl.getSizeOfArray()) {
			shiftLength = wl.getSizeOfArray() - start;
		}

		calculateNewWorkloads(start, start + shiftLength, capacity);
		calculateCapacities(start, start + shiftLength, capacity);
		for (int x = start; x < start + shiftLength; x++) {
			// Increase the count of the number of physicians used each hour for the 12-hour
			// slot
			clinicianCounter[x]++;
		}

		wl.getHourlyDetailList()[start].incrementNumberOfShiftBeginning();
		wl.getHourlyDetailList()[start + shiftLength - 1].incrementNumberOfShiftEnding();// check for clinicians
		wl.getResult().add(newShift);
	}

	public Shift getNewShift(int numberOfHours, int start, String physicianType) {
		Shift newShift = new Shift();
		newShift.setStartTime(start % wl.getDayDuration());
		newShift.setEndTime((start + numberOfHours) % wl.getDayDuration());
		newShift.setDay(start / wl.getDayDuration());
		newShift.setNoOfHours(numberOfHours);
		newShift.setPhysicianType(physicianType);
		return newShift;
	}

	public void calculateNewWorkloads(int start, int end, Double[] capacity) {
		// Reduces the workloads as per the capacities of each physician per hour
		for (int i = start; i < end; i++) {
			if (i == start) {
				wl.getWorkloadArray()[i] = round(wl.getWorkloadArray()[i] - capacity[0], 2);
			} else if (i == end - 1) {
				wl.getWorkloadArray()[i] = round(wl.getWorkloadArray()[i] - capacity[2], 2);

			} else {
				wl.getWorkloadArray()[i] = round(wl.getWorkloadArray()[i] - capacity[1], 2);
			}
		}

	}

	public void calculateCapacities(int start, int end, Double[] capacity) {
		for (int i = start; i < end; i++) {
			if (i == start) {
				wl.getCapacityArray()[i] = round(wl.getCapacityArray()[i] + capacity[0], 2);
			}

			else if (i == end - 1) {
				wl.getCapacityArray()[i] = round(wl.getCapacityArray()[i] + capacity[2], 2);

			} else {
				wl.getCapacityArray()[i] = round(wl.getCapacityArray()[i] + capacity[1], 2);
			}
		}
	}

	public double round(double value, int places) {
		long factor = (long) Math.pow(10, places);
		value = value * factor;
		double tmp = Math.round(value);
		return tmp / factor;
	}

	public double min(double a, double b) {
		if (a > b)
			return b;
		else
			return a;
	}

	public List<List<Shift>> printSlots() {
		List<List<Shift>> dayToShiftMapping = new ArrayList<>(); // Maps --- Day --->> shift-slots per day
		for (int i = 0; i < 7; i++) // Loop through List with foreach
		{
			List<Shift> shiftList = new ArrayList<>();
			for (Shift s : wl.getResult()) {
				if (s.getDay() == i) {
					shiftList.add(s);
				}
			}
			dayToShiftMapping.add(shiftList);
		}
		return dayToShiftMapping;
	}

	double diff[] = new double[168];
	double computingDiff[] = new double[168];
	double wait[] = new double[168];
	double loss[] = new double[168];
	int count[] = new int[168];

	public HourlyDetail[] generateHourlyDetail(int patientHourWait, Clinician[] clinicians, double docEfficiency,
			double lowerLimitFactor) {
		HourlyDetail[] hourlyDetailList = wl.getHourlyDetailList();

		for (int i = 0; i < 168; i++) { // Assuming the order of clinicians is physician, app and scribe

			hourlyDetailList[i].setNumberOfPhysicians(clinicians[0].getClinicianCountPerHour()[i]);
			hourlyDetailList[i].setNumberOfAPPs(clinicians[1].getClinicianCountPerHour()[i]);
			hourlyDetailList[i].setNumberOfScribes(clinicians[2].getClinicianCountPerHour()[i]);
			hourlyDetailList[i].setExpectedWorkLoad(wl.getFixedworkloadArray()[i]);
			hourlyDetailList[i].setCapacityWorkLoad(wl.getCapacityArray()[i]);
			hourlyDetailList[i].setHour(i);
			hourlyDetailList[i].setUtilization(wl.getFixedworkloadArray()[i] / wl.getCapacityArray()[i]);
			hourlyDetailList[i].setCostPerHour((clinicians[0].getClinicianCountPerHour()[i] * clinicians[0].getCost())
					+ (clinicians[1].getClinicianCountPerHour()[i] * clinicians[1].getCost())
					+ (clinicians[2].getClinicianCountPerHour()[i] * clinicians[2].getCost()));
			diff[i] = hourlyDetailList[i].getCapacityWorkLoad() - hourlyDetailList[i].getExpectedWorkLoad();

		}
		if (patientHourWait == 1) { // one hour wait
			for (int i = 1; i < 168; i++) {
				computingDiff[i] = diff[i];
				if (computingDiff[i - 1] < 0 && computingDiff[i] < 0)// in previous hour,some patients are not handled
				// even in this hour also, some patients are waiting
				{
					wait[i] = 0;
					loss[i] = computingDiff[i - 1];
				} else if (computingDiff[i - 1] < 0 && computingDiff[i] > 0)// handling patients who are waiting in
																			// previous hour
				// using current capacity.
				{
					wait[i] = 0;
					loss[i] = min(0, computingDiff[i - 1] + computingDiff[i]);
				}
			}

		}

		if (patientHourWait > 1) { // more than one hour wait
			for (int i = 0; i < 168; i++) {
				computingDiff[i] = diff[i];
				if (i == 0) // index is 0
				{
					wait[i] = 0;
					loss[i] = 0;
					count[i] = 1;
				} else {
					int index = i - patientHourWait;
					int startOfSlidingWindow = 0;
					if (index >= 0) // sliding window is possible
					{
						startOfSlidingWindow = index;
					}
					for (int j = startOfSlidingWindow; j <= i; j++) // acting like sliding window
					{
						count[j]++;
					}
					if (wait[i - 1] == 0 && computingDiff[i - 1] < 0) // previous hour_wait ==0 and previous hour_diff
																		// <0
					{
						if (computingDiff[i] >= 0) { // current hour_diff >=0
							double addedValue = computingDiff[i] + computingDiff[i - 1];
							wait[i] = min(0, computingDiff[i] + computingDiff[i - 1]);
							computingDiff[i - 1] = wait[i];
							if (addedValue < 0) // updating array for further processing
							{
								computingDiff[i] = 0;
							} else {
								computingDiff[i] = addedValue;
							}
						} else {
							wait[i] = computingDiff[i - 1];
						}
						loss[i] = 0;
					} else if (wait[i - 1] < 0 && computingDiff[i] < 0) // previous hour_wait <0 and current hour_diff
																		// <0
					{
						int index1 = i - patientHourWait;
						if (index1 >= 0 && count[index1] == patientHourWait + 1 && computingDiff[index1] < 0) {
							loss[i] = computingDiff[index1];
							wait[i] = wait[i - 1] + computingDiff[i - 1] - loss[i];
						} else {
							loss[i] = 0;
							wait[i] = wait[i - 1] + computingDiff[i - 1];
						}
					} else if (wait[i - 1] < 0 && computingDiff[i] >= 0) // previous hour_wait <0 and current hour_diff
																			// >=0
					{
						int index1 = i - patientHourWait;
						if (index1 < 0) {
							index1 = 0;
						}
						double currentDiffValue = computingDiff[i];
						double sum = wait[i - 1] + computingDiff[i - 1];

						if (Math.abs(sum) < computingDiff[i]) // current hour_diff can handle all patients who are
																// waiting
						// from previous hour
						{
							wait[i] = 0;
							loss[i] = 0;
							computingDiff[i] = 0;
							for (int g = index1; g < i; g++) {
								computingDiff[g] = 0;
							}
						} else {
							for (int g = index1; g < i && currentDiffValue >= 0; g++) {
								if (computingDiff[g] >= 0) // already patients are handled
									continue;
								else {
									double addedValue = currentDiffValue + computingDiff[g];
									if (index1 >= 0 && addedValue < 0 && count[g] == patientHourWait + 1) {
										computingDiff[g] = addedValue;
										loss[i] = computingDiff[g];
										currentDiffValue = 0;
										computingDiff[i] = 0;
									} else if (addedValue < 0) {
										computingDiff[g] = addedValue;
										computingDiff[i] = 0;
										currentDiffValue = 0;
									} else {
										currentDiffValue = addedValue;
										computingDiff[g] = 0;
										computingDiff[i] = currentDiffValue;
									}
								}
							}

							for (int g = index1 + 1; g < i; g++) {
								wait[i] += computingDiff[g];
							}

						}
					} else // no previous conditions are true
					{
						wait[i] = 0;
						loss[i] = 0;
					}
					if (wait[i] > 0) {
						wait[i] = 0;
					}
				}

				if (computingDiff[i] >= 0) {
					computingDiff[i] = 0;
				}
			}
		}
		log.info("---------------------------------");
		log.info(" D.Output:: HourlyDetails ");
		log.info("---------------------------------");
		for (int i = 0; i < 168; i++) {
			hourlyDetailList[i].setLoss(loss[i]);
			hourlyDetailList[i].setWait(wait[i]);
			log.info(" D." + (i) + ".1 hour:" + i + ", D." + (i) + ".2 numberOfPhysicians :"
					+ hourlyDetailList[i].getNumberOfPhysicians() + ", D." + (i) + ".3 numberOfAPPs :"
					+ hourlyDetailList[i].getNumberOfAPPs() + ", D." + (i) + ".4 numberOfScribes :"
					+ hourlyDetailList[i].getNumberOfScribes() + ", D." + (i) + ".5 numberOfShiftBeginning :"
					+ hourlyDetailList[i].getNumberOfShiftBeginning() + ", D." + (i) + ".6 numberOfShiftEnding :"
					+ hourlyDetailList[i].getNumberOfShiftEnding() + ", D." + (i) + ".7 expectedWorkLoad :"
					+ hourlyDetailList[i].getExpectedWorkLoad() + ", D." + (i) + ".8 capacityWorkLoad :"
					+ hourlyDetailList[i].getCapacityWorkLoad() + ", D." + (i) + ".9 Difference :"
					+ (hourlyDetailList[i].getCapacityWorkLoad() - hourlyDetailList[i].getExpectedWorkLoad()) + ", D."
					+ (i) + ".10 utilization :" + hourlyDetailList[i].getUtilization() + ", D." + (i)
					+ ".11 costPerHour :" + hourlyDetailList[i].getCostPerHour() + ", D." + (i) + ".12 patientWait :"
					+ hourlyDetailList[i].getWait() + ", D." + (i) + ".13 patientLoss :"
					+ hourlyDetailList[i].getLoss());
		}
		return hourlyDetailList;
	}
}