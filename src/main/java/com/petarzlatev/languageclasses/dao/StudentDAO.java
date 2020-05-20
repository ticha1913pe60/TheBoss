package com.petarzlatev.languageclasses.dao;

import java.sql.SQLException;
import java.util.List;

import com.petarzlatev.languageclasses.model.Student;

public interface StudentDAO {

	/****************************************************
	 * CRUD *
	 ****************************************************/

	List<Student> loadStudents() throws SQLException;

	int addStudent(String firstName, String lastName, String phone, double rate) throws SQLException;

	boolean updateStudent(String firstName, String lastName, String phone, double rate, int id) throws SQLException;

}