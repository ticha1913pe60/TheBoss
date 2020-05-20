package com.petarzlatev.languageclasses.model;

import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

@Entity
@Table(name = "APP_STUDENTS")
public class Student extends Person {
	private SimpleStringProperty phoneNumber;
	private SimpleDoubleProperty ratePerHour;
	@OneToMany(mappedBy = "atendingStudent", cascade = CascadeType.ALL)
	private List<Lesson> lessons;

	public Student() {

	}

	public Student(String firstName, String lastName, String phone, double rate, int personID) {
		super(firstName, lastName);
		setPhoneNumber(phone);
		setRatePerHour(rate);
		setPersonID(personID);
	}

	public void update(String firstName, String lastName, String phone, double rate) {
		setFirstName(firstName);
		setLastName(lastName);
		setPhoneNumber(phone);
		setRatePerHour(rate);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "RATE")
	public double getRatePerHour() {
		return ratePerHour.get();
	}

	public void setRatePerHour(double ratePerHour) {
		if (this.ratePerHour == null) {
			this.ratePerHour = new SimpleDoubleProperty();
		}
		this.ratePerHour.set(ratePerHour);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "PHONE")
	public String getPhoneNumber() {
		return phoneNumber.get();
	}

	public void setPhoneNumber(String phoneNumber) {
		if (this.phoneNumber == null) {
			this.phoneNumber = new SimpleStringProperty();
		}
		this.phoneNumber.set(phoneNumber);
	}

	public List<Lesson> getLessons() {
		return lessons;
	}

	public void setLessons(List<Lesson> lessons) {
		this.lessons = lessons;
	}

}
