package com.petarzlatev.languageclasses.dao.oracle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.petarzlatev.languageclasses.dao.PersonDAO;
import com.petarzlatev.languageclasses.dao.UserDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Person;
import com.petarzlatev.languageclasses.model.User;

public class OracleUserDAO implements PersonDAO, UserDAO {
	private final Connection conn = DataSource.getInstance().getConnection();

	/****************************************************
	 * SQL *
	 ****************************************************/
	private static final String QUERY_USER = "SELECT * FROM " + DataSource.TABLE_USERS + " WHERE "
			+ DataSource.COLUMN_USERS_USERNAME + " = ?";
	private static final String QUERY_USER_LIST = "SELECT * FROM " + DataSource.TABLE_USERS;

	private static final String INSERT_USER = "INSERT INTO " + DataSource.TABLE_USERS + " ( "
			+ DataSource.COLUMN_USERS_FIRSTNAME + ", " + DataSource.COLUMN_USERS_LASTNAME + ", "
			+ DataSource.COLUMN_USERS_USERNAME + ",  " + DataSource.COLUMN_USERS_PASSWORD + ",  "
			+ DataSource.COLUMN_USERS_IS_ADMIN + ", " + DataSource.COLUMN_USERS_SALT + " ) VALUES( ?, ?, ?, ?, ?, ? )";

	private static final String UPDATE_USER = "UPDATE " + DataSource.TABLE_USERS + " SET "
			+ DataSource.COLUMN_USERS_USERNAME + " = ?, " + DataSource.COLUMN_USERS_PASSWORD + " = ?, "
			+ DataSource.COLUMN_USERS_FIRSTNAME + " = ?, " + DataSource.COLUMN_USERS_LASTNAME + " = ?, "
			+ DataSource.COLUMN_USERS_IS_ADMIN + " = ? WHERE " + DataSource.COLUMN_USERS_ID_USER + " = ?";

	private static final String DELETE_USER = "DELETE FROM " + DataSource.TABLE_USERS + " WHERE "
			+ DataSource.COLUMN_USERS_ID_USER + " = ?";

	/****************************************************
	 * CRUD *
	 ****************************************************/

	@Override
	public User loadCurrentUser(String username) throws SQLException {
		PreparedStatement queryUser = conn.prepareStatement(QUERY_USER);
		queryUser.setString(1, username);
		ResultSet results = queryUser.executeQuery();
		if (results.next()) {
			return new User(results.getString(DataSource.COLUMN_USERS_FIRSTNAME),
					results.getString(DataSource.COLUMN_USERS_LASTNAME),
					results.getString(DataSource.COLUMN_USERS_USERNAME),
					results.getString(DataSource.COLUMN_USERS_PASSWORD),
					results.getString(DataSource.COLUMN_USERS_IS_ADMIN),
					results.getInt(DataSource.COLUMN_USERS_ID_USER), results.getString(DataSource.COLUMN_USERS_SALT));
		}
		queryUser.close();

		return null;
	}

	@Override
	public List<User> loadUsers() throws SQLException {
		PreparedStatement queryUserList = conn.prepareStatement(QUERY_USER_LIST);
		ResultSet results = queryUserList.executeQuery();
		List<User> users = new ArrayList<User>();
		while (results.next()) {
			users.add(new User(results.getString(DataSource.COLUMN_USERS_FIRSTNAME),
					results.getString(DataSource.COLUMN_USERS_LASTNAME),
					results.getString(DataSource.COLUMN_USERS_USERNAME),
					results.getString(DataSource.COLUMN_USERS_PASSWORD),
					results.getString(DataSource.COLUMN_USERS_IS_ADMIN),
					results.getInt(DataSource.COLUMN_USERS_ID_USER), results.getString(DataSource.COLUMN_USERS_SALT)));
		}
		queryUserList.close();

		return users;
	}

	@Override
	public int addUser(String firstName, String lastName, String username, String password, String isAdmin, String salt)
			throws SQLException {
		String returnCols[] = { DataSource.COLUMN_USERS_ID_USER };
		PreparedStatement insertUser = conn.prepareStatement(INSERT_USER, returnCols);
		PreparedStatement queryUser = conn.prepareStatement(QUERY_USER);
		int newID = 0;
		queryUser.setString(1, username);
		ResultSet results = queryUser.executeQuery();
		if (!results.next()) {
			insertUser.setString(1, firstName);
			insertUser.setString(2, lastName);
			insertUser.setString(3, username);
			insertUser.setString(4, password);
			insertUser.setString(5, isAdmin);
			insertUser.setString(6, salt);
			int affectedRows = insertUser.executeUpdate();

			if (affectedRows != 1) {
				throw new SQLException("Couldn't insert student!");
			} else {
				ResultSet generatedKeys = insertUser.getGeneratedKeys();
				if (generatedKeys.next()) {
					newID = generatedKeys.getInt(1);
				}
			}
		}
		queryUser.close();
		insertUser.close();

		return newID;
	}

	@Override
	public boolean updateUser(String firstName, String lastName, String username, String password, String isAdmin,
			int id) throws SQLException {
		PreparedStatement updateUser = conn.prepareStatement(UPDATE_USER);
		updateUser.setString(1, username);
		updateUser.setString(2, password);
		updateUser.setString(3, firstName);
		updateUser.setString(4, lastName);
		updateUser.setString(5, isAdmin);
		updateUser.setInt(6, id);
		int affectedRows = updateUser.executeUpdate();

		if (affectedRows != 1) {
			throw new SQLException("Couldn't update student!");
		}
		updateUser.close();

		return true;
	}

	@Override
	public boolean deleteUser(int id) throws SQLException {
		PreparedStatement deleteUser = conn.prepareStatement(DELETE_USER);
		deleteUser.setInt(1, id);
		int affectedRows = deleteUser.executeUpdate();

		if (affectedRows != 1) {
			throw new SQLException("Couldn't delete user!");
		}
		deleteUser.close();

		return true;
	}

	@Override
	public boolean deletePerson(Person person) throws SQLException {
		return deleteUser(person.getPersonID());
	}

}
