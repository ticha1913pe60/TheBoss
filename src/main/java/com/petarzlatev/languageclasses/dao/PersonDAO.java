package com.petarzlatev.languageclasses.dao;

import java.sql.SQLException;

import com.petarzlatev.languageclasses.model.Person;

public interface PersonDAO {
	boolean deletePerson(Person person) throws SQLException;
}
