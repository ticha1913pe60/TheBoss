package com.petarzlatev.languageclasses.dao;

import java.sql.SQLException;
import java.util.List;

import com.petarzlatev.languageclasses.model.User;

public interface UserDAO {

	/****************************************************
	 * CRUD *
	 ****************************************************/

	User loadCurrentUser(String username) throws SQLException;

	List<User> loadUsers() throws SQLException;

	int addUser(String firstName, String lastName, String username, String password, String isAdmin, String salt)
			throws SQLException;

	boolean updateUser(String firstName, String lastName, String username, String password, String isAdmin, int id)
			throws SQLException;

}