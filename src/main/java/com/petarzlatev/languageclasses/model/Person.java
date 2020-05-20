package com.petarzlatev.languageclasses.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import javafx.beans.property.SimpleStringProperty;

@MappedSuperclass
public abstract class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "ID_PERSON")
	private int personID;
	private SimpleStringProperty firstName;
	private SimpleStringProperty lastName;

	public Person() {
		this.setPersonID(0);
		this.firstName = new SimpleStringProperty();
		this.lastName = new SimpleStringProperty();
	}

	public Person(String firstName, String lastName) {
		this();
		setFirstName(firstName);
		setLastName(lastName);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "FIRSTNAME")
	public String getFirstName() {
		return firstName.get();
	}

	public void setFirstName(String firstName) {
		this.firstName.set(firstName);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "LASTNAME")
	public String getLastName() {
		return lastName.get();
	}

	public void setLastName(String lastName) {
		this.lastName.set(lastName);
	}

	public String getFullName() {
		String fullName = "";

		if (!getFirstName().isEmpty()) {
			fullName = getFirstName() + " ";
		}
		if (!getLastName().isEmpty()) {
			if (fullName.isEmpty()) {
				fullName = getLastName();
			} else {
				fullName += getLastName();
			}
		}

		return fullName.trim();
	}

	public int getPersonID() {
		return personID;
	}

	public void setPersonID(int personID) {
		this.personID = personID;
	}
}
