package com.envision.Staffing.model;

public class DayShift {

	private String day;
	private int shiftLength;
	private int startTime;
	private int endTime;
	private int physician;
	private int app;
	private int scribe;


	public int getName(String name) {
		if (name.equals("physician")) {
			return this.getPhysician();
		} else if (name.equals("app")) {
			return this.getApp();
		} else {
			return this.getScribe();
		}
	}

	public void setName(String name, int clinician) {
		if (name.equals("physician")) {
			this.setPhysician(clinician);
		} else if (name.equals("app")) {
			this.setApp(clinician);
		} else {
			this.setScribe(clinician);
		}
	}

	public int getShiftLength() {
		return shiftLength;
	}

	public void setShiftLength(int shiftLength) {
		this.shiftLength = shiftLength;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	public int getPhysician() {
		return physician;
	}

	public void setPhysician(int physician) {
		this.physician = physician;
	}

	public int getApp() {
		return app;
	}

	public void setApp(int app) {
		this.app = app;
	}

	public int getScribe() {
		return scribe;
	}

	public void setScribe(int scribe) {
		this.scribe = scribe;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return "DayShift [day=" + day + ", shiftLength=" + shiftLength + ", startTime=" + startTime + ", endTime="
				+ endTime + ", physician=" + physician + ", app=" + app + ", scribe=" + scribe + "]";
	}
}
