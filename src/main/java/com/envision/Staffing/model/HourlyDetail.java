package com.envision.Staffing.model;

import java.io.Serializable;

//model to hold the hourly details containing the hour and the count of clinicians
//in each hour, count of clinicians starting their shifts and ending their shifts
public class HourlyDetail implements Serializable {

	private Integer hour; // denotes the hour [0-167]

	private Integer numberOfPhysicians;

	private Integer numberOfAPPs;

	private Integer numberOfScribes;

	private Integer numberOfShiftBeginning; // number of clinicians beginning their shifts includes all physicians,
											// scribes and apps

	private Integer numberOfShiftEnding;// number of clinicians ending their shifts includes all physicians,
										// scribes and apps

	private Double expectedWorkLoad;

	private Double capacityWorkLoad;// workload that can be handled by the clincians allotted

	private Double utilization; // utilization percentage of the clinicians

	private Integer costPerHour;// cost for each hour considering all the clinicians
	private double loss;
	private double wait;

	// constructor
	public HourlyDetail() {
		this.numberOfShiftBeginning = 0;
		this.numberOfShiftEnding = 0;
		this.costPerHour = 0;
	}

	// getters and setters for all the fields

	public Integer getHour() {
		return hour;
	}

	public double getLoss() {
		return loss;
	}

	public void setLoss(double loss) {
		this.loss = loss;
	}

	public double getWait() {
		return wait;
	}

	public void setWait(double wait) {
		this.wait = wait;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Integer getNumberOfPhysicians() {
		return numberOfPhysicians;
	}

	public void setNumberOfPhysicians(Integer numberOfPhysicians) {
		this.numberOfPhysicians = numberOfPhysicians;
	}

	public Integer getNumberOfShiftBeginning() {
		return numberOfShiftBeginning;
	}

	public void setNumberOfShiftBeginning(Integer numberOfShiftBeginning) {
		this.numberOfShiftBeginning = numberOfShiftBeginning;
	}

	public Integer getNumberOfShiftEnding() {
		return numberOfShiftEnding;
	}

	public void setNumberOfShiftEnding(Integer numberOfShiftEnding) {
		this.numberOfShiftEnding = numberOfShiftEnding;
	}

	public Double getExpectedWorkLoad() {
		return expectedWorkLoad;
	}

	public void setExpectedWorkLoad(Double expectedWorkLoad) {
		this.expectedWorkLoad = expectedWorkLoad;
	}

	public Double getCapacityWorkLoad() {
		return capacityWorkLoad;
	}

	public void setCapacityWorkLoad(Double capacityWorkLoad) {
		this.capacityWorkLoad = capacityWorkLoad;
	}

	public Double getUtilization() {
		return utilization;
	}

	public void setUtilization(Double utilization) {
		this.utilization = utilization;
	}

	public void incrementNumberOfShiftBeginning() {
		if (numberOfShiftBeginning == null)
			numberOfShiftBeginning = 1;
		else
			this.numberOfShiftBeginning++;
	}

	public void incrementNumberOfShiftEnding() {
		if (numberOfShiftEnding == null)
			numberOfShiftEnding = 1;
		else
			this.numberOfShiftEnding++;
	}

	public Integer getNumberOfAPPs() {
		return numberOfAPPs;
	}

	public void setNumberOfAPPs(Integer numberOfAPPs) {
		this.numberOfAPPs = numberOfAPPs;
	}

	public Integer getNumberOfScribes() {
		return numberOfScribes;
	}

	public void setNumberOfScribes(Integer numberOfScribes) {
		this.numberOfScribes = numberOfScribes;
	}

	public Integer getCostPerHour() {
		return this.costPerHour;
	}

	public void setCostPerHour(Integer costPerHour) {
		this.costPerHour = costPerHour;
	}

	@Override
	public String toString() {
		return "HourlyDetail [hour=" + hour + ", numberOfPhysicians=" + numberOfPhysicians + ", numberOfAPPs="
				+ numberOfAPPs + ", numberOfScribes=" + numberOfScribes + ", expectedWorkLoad=" + expectedWorkLoad
				+ ", capacityWorkLoad=" + capacityWorkLoad + ", utilization=" + utilization + ", costPerHour="
				+ costPerHour + ", loss=" + loss + ", wait=" + wait + "]";
	}

}