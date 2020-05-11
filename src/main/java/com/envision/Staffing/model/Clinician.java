package com.envision.Staffing.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.envision.Staffing.converter.DoubleArrayToStringConverter;
import com.envision.Staffing.converter.StringListConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//Model to hold data about a clinician
@Entity
@Table(name = "clinician_details")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Clinician implements Comparable<Clinician>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
	private String id;

	@Column(name = "role")
	private String name; // clinician description

	@Column(name = "patients_per_hour")
	private Double patientsPerHour; // indicates the number of patients the clinician can handle in an hour

	@Transient
	private double coefficient; // denotes the percentage with respect to physician

	@Column(name = "cost_per_hour")
	private int cost; // cost of each clinician per hour

	@Column(name = "capacity")
	@Convert(converter = DoubleArrayToStringConverter.class)
	private Double[] capacity; // for first hour, mid hour and last hour

	@Transient
	private int[] clinicianCountPerHour;

	@Transient
	private List<String> expressionsString;
	
	
	@Column(name = "expressions")
	@Convert(converter = StringListConverter.class)
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
	private  List<String> expressions; // expressions to handle the relationships between clinicians

	// constructor
	public Clinician(String id, String name, Double patientsPerHour, double coefficient, int cost, Double[] capacity) {
		this.id = id;
		this.name = name;
		this.patientsPerHour = patientsPerHour;
		this.coefficient = coefficient;
		this.cost = cost;
		this.capacity = capacity;
	}

	// getters and setters for each field
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPatientsPerHour() {
		return patientsPerHour;
	}

	public void setPatientsPerHour(Double patientsPerHour) {
		this.patientsPerHour = patientsPerHour;
	}

	public double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(double coefficient) {
		this.coefficient = coefficient;
	}

	public int getCost() {
		return this.cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public Double[] getCapacity() {
		return capacity;
	}

	public void setCapacity(Double[] capacity) {
		this.capacity = capacity;
	}

	public int[] getClinicianCountPerHour() {
		return clinicianCountPerHour;
	}

	public void setClinicianCountPerHour(int[] clinicianCountPerHour) {
		this.clinicianCountPerHour = clinicianCountPerHour;
	}
//
//	public String[] getExpressions() {
//		return expressions;
//	}
//
//	public void setExpressions(String[] expressions) {
//		this.expressions = expressions;
//	}

	public Clinician() {
		super();
	}

	public List<String> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<String> expressions) {
		this.expressions = expressions;
	}

	// overriding compare to function, to provide sort on clinician objects based on
	// the clinician's cost in ascending order
	public int compareTo(Clinician cli) {
		int comparecost = cli.getCost();
		// ascending order
		return this.cost - comparecost;

	}

	public List<String> getExpressionsString() {
		return expressionsString;
	}

	public void setExpressionsString(List<String> expressionsString) {
		this.expressionsString = expressionsString;
	}

}
