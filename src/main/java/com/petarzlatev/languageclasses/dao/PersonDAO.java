package com.petarzlatev.languageclasses.dao;

import java.sql.SQLException;

import com.petarzlatev.languageclasses.model.Person;

public interface PersonDAO {
	public boolean deletePerson(Person person) throws SQLException;
}
