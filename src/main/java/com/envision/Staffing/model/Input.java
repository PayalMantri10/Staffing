package com.envision.Staffing.model;

public class Input {

	private Clinician[] clinician;
	private Integer[] shiftLength;
	private Double lowerLimitFactor;
	private Double upperLimitFactor;
	private Day[] dayWorkload;
	private Integer notAllocatedStartTime;
	private Integer notAllocatedEndTime;
	private Integer patientHourWait;

	public Clinician[] getClinician() {
		return clinician;
	}

	public void setClinician(Clinician[] clinician) {
		this.clinician = clinician;
	}

	public Integer[] getShiftLength() {
		return shiftLength;
	}

	public void setShiftLength(Integer[] shiftLength) {
		this.shiftLength = shiftLength;
	}

	public Double getLowerLimitFactor() {
		return lowerLimitFactor;
	}

	public void setLowerLimitFactor(double lowerLimitFactor) {
		this.lowerLimitFactor = lowerLimitFactor;
	}

	public Day[] getDayWorkload() {
		return dayWorkload;
	}

	public void setDayWorkload(Day[] dayWorkload) {
		this.dayWorkload = dayWorkload;
	}

	public Double getUpperLimitFactor() {
		return upperLimitFactor;
	}

	public void setUpperLimitFactor(Double upperLimitFactor) {
		this.upperLimitFactor = upperLimitFactor;
	}

	public Integer getNotAllocatedStartTime() {
		return notAllocatedStartTime;
	}

	public void setNotAllocatedStartTime(Integer notAllocatedStartTime) {
		this.notAllocatedStartTime = notAllocatedStartTime;
	}

	public Integer getNotAllocatedEndTime() {
		return notAllocatedEndTime;
	}

	public void setNotAllocatedEndTime(Integer notAllocatedEndTime) {
		this.notAllocatedEndTime = notAllocatedEndTime;
	}

	public Integer getPatientHourWait() {
		return patientHourWait;
	}

	public void setPatientHourWait(Integer patientHourWait) {
		this.patientHourWait = patientHourWait;
	}
	

}