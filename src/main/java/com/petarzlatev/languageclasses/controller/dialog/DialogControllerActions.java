package com.petarzlatev.languageclasses.controller.dialog;

import java.sql.SQLException;

import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.model.Person;

public interface DialogControllerActions {
	public boolean setData(Person person);
	public Person processData(SystemLibrary.OperType operType, Person user) throws SQLException;
	public boolean validateData();
}

