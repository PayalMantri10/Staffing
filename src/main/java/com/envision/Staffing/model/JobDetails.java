package com.envision.Staffing.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.envision.Staffing.converter.DoubleArrayToStringConverter;
import com.envision.Staffing.converter.IntegerArrayToStringConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "job_details")
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "email_id")
	private String outputEmailId;

	@Column(name = "status")
	private String status;

	public String getOutputEmailId() {
		return outputEmailId;
	}

	public void setOutputEmailId(String outputEmailId) {
		this.outputEmailId = outputEmailId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	@Column(name = "user_id")
	private String userId;

	@Column(name = "shift_length_preferences")
	@Convert(converter = IntegerArrayToStringConverter.class)
	private Integer[] shiftLengthPreferences; // for first hour, mid hour and last hour

	@Column(name = "lower_utilization_factor")
	private Float lowerUtilizationFactor;

	@Column(name = "upper_utilization_factor")
	private Float upperUtilizationFactor;

	@Column(name = "schedule_datetime")
	private Date scheduledDate;

	@Column(name = "job_name")
	private String name;

	@Column(name = "input_format")
	private String inputFormat;

	@Column(name = "cron_expression")
	private String cronExpression;

	@Column(name = "not_allocated_start_time")
	private Integer notAllocatedStartTime;

	@Column(name = "not_allocated_end_time")
	private Integer notAllocatedEndTime;

	@Column(name = "patient_hour_wait")
	private Integer patientHourWait;

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
	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "input_ftp_id")
	private FtpDetails inputFtpDetails;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "input_file_id")
	private FileDetails inputFileDetails;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "output_ftp_id")
	private FtpDetails outputFtpDetails;

	@Column(name = "output_format")
	private String outputFormat;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "job_id", referencedColumnName = "id")
	private List<Clinician> clinicians;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Integer[] getShiftLengthPreferences() {
		return shiftLengthPreferences;
	}

	public void setShiftLengthPreferences(Integer[] shiftLengthPreferences) {
		this.shiftLengthPreferences = shiftLengthPreferences;
	}

	public Float getLowerUtilizationFactor() {
		return lowerUtilizationFactor;
	}

	public void setLowerUtilizationFactor(Float lowerUtilizationFactor) {
		this.lowerUtilizationFactor = lowerUtilizationFactor;
	}

	public Float getUpperUtilizationFactor() {
		return upperUtilizationFactor;
	}

	public void setUpperUtilizationFactor(Float upperUtilizationFactor) {
		this.upperUtilizationFactor = upperUtilizationFactor;
	}

	public Date getScheduledDate() {
		return scheduledDate;
	}

	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInputFormat() {
		return inputFormat;
	}

	public void setInputFormat(String inputFormat) {
		this.inputFormat = inputFormat;
	}

	public FtpDetails getInputFtpDetails() {
		return inputFtpDetails;
	}

	public void setInputFtpDetails(FtpDetails inputFtpDetails) {
		this.inputFtpDetails = inputFtpDetails;
	}

	public FtpDetails getOutputFtpDetails() {
		return outputFtpDetails;
	}

	public void setOutputFtpDetails(FtpDetails outputFtpDetails) {
		this.outputFtpDetails = outputFtpDetails;
	}

	public List<Clinician> getClinicians() {
		return clinicians;
	}

	public void setClinicians(List<Clinician> clinicians) {
		this.clinicians = clinicians;
	}

	public FileDetails getInputFileDetails() {
		return inputFileDetails;
	}

	public void setInputFileDetails(FileDetails inputFileDetails) {
		this.inputFileDetails = inputFileDetails;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

}
