package com.petarzlatev.languageclasses.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import javafx.beans.property.SimpleStringProperty;

@Entity
@Table(name = "APP_USERS")
public class User extends Person {
	private SimpleStringProperty username;
	private SimpleStringProperty password;
	private SimpleStringProperty salt;
	private SimpleStringProperty isAdmin;

	public User() {

	}

	public User(String firstName, String lastName, String username, String password, String isAdmin, int personID,
			String salt) {
		super(firstName, lastName);
		setUsername(username);
		setSalt(salt);
		setPassword(password);
		setIsAdmin(isAdmin);
		setPersonID(personID);
	}

	public void update(String firstName, String lastName, String username, String password, String isAdmin) {
		setFirstName(firstName);
		setLastName(lastName);
		setUsername(username);
		setPassword(password);
		setIsAdmin(isAdmin);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "IS_ADMIN")
	public String getIsAdmin() {
		return isAdmin.get();
	}

	public void setIsAdmin(String isAdmin) {
		if (this.isAdmin == null) {
			this.isAdmin = new SimpleStringProperty();
		}
		this.isAdmin.set(isAdmin);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "USERNAME")
	public String getUsername() {
		return username.get();
	}

	public void setUsername(String username) {
		if (this.username == null) {
			this.username = new SimpleStringProperty();
		}
		this.username.set(username);
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "PASSWORD")
	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		if (this.password == null) {
			this.password = new SimpleStringProperty();
		}
		this.password.set(password);
	}

	public boolean isAdmin() {
		return getIsAdmin().equals("T");
	}

	@Access(AccessType.PROPERTY)
	@Column(name = "SALT")
	public String getSalt() {
		return salt.get();
	}

	public void setSalt(String salt) {
		if (this.salt == null) {
			this.salt = new SimpleStringProperty();
		}
		this.salt.set(salt);
	}

}
