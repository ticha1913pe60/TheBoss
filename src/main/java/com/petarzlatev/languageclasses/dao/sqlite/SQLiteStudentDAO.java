package com.petarzlatev.languageclasses.dao.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.petarzlatev.languageclasses.dao.PersonDAO;
import com.petarzlatev.languageclasses.dao.StudentDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Person;
import com.petarzlatev.languageclasses.model.Student;

public class SQLiteStudentDAO implements PersonDAO, StudentDAO {
	private final Connection conn = DataSource.getInstance().getConnection();

	/****************************************************
	 * SQL *
	 ****************************************************/
	private static final String QUERY_STUDENT_LIST = "SELECT * FROM " + DataSource.TABLE_STUDENTS;
	private static final String QUERY_STUDENT_BY_NAME = "SELECT " + DataSource.COLUMN_STUDENTS_FIRSTNAME + " FROM "
			+ DataSource.TABLE_STUDENTS + " WHERE " + DataSource.COLUMN_STUDENTS_FIRSTNAME + " = ?" + " AND "
			+ DataSource.COLUMN_STUDENTS_LASTNAME + " = ?";
	
	private static final String INSERT_STUDENT = "INSERT INTO " + DataSource.TABLE_STUDENTS + " ( "
			+ DataSource.COLUMN_STUDENTS_FIRSTNAME + ", " + DataSource.COLUMN_STUDENTS_LASTNAME + ", "
			+ DataSource.COLUMN_STUDENTS_PHONE + ",  " + DataSource.COLUMN_STUDENTS_RATE + " ) VALUES( ?, ?, ?, ? )";

	private static final String UPDATE_STUDENT = "UPDATE " + DataSource.TABLE_STUDENTS + " SET "
			+ DataSource.COLUMN_STUDENTS_FIRSTNAME + " = ?, " + DataSource.COLUMN_STUDENTS_LASTNAME + " = ?, "
			+ DataSource.COLUMN_STUDENTS_PHONE + " = ?,  " + DataSource.COLUMN_STUDENTS_RATE + " = ? WHERE "
			+ DataSource.COLUMN_STUDENTS_ID_STUDENT + " = ?";
	
	private static final String DELETE_STUDENT = "DELETE FROM " + DataSource.TABLE_STUDENTS + " WHERE "
			+ DataSource.COLUMN_STUDENTS_ID_STUDENT + " = ?";

	/****************************************************
	 * CRUD *
	 ****************************************************/
	@Override
	public List<Student> loadStudents() throws SQLException {
		PreparedStatement queryStudentList = conn.prepareStatement(QUERY_STUDENT_LIST);

		ResultSet results = queryStudentList.executeQuery();
		List<Student> students = new ArrayList<Student>();
		while (results.next()) {
			students.add(new Student(results.getString(DataSource.COLUMN_STUDENTS_FIRSTNAME),
					results.getString(DataSource.COLUMN_STUDENTS_LASTNAME),
					results.getString(DataSource.COLUMN_STUDENTS_PHONE),
					results.getDouble(DataSource.COLUMN_STUDENTS_RATE),
					results.getInt(DataSource.COLUMN_STUDENTS_ID_STUDENT)));
		}
		queryStudentList.close();

		return students;
	}

	@Override
	public int addStudent(String firstName, String lastName, String phone, double rate) throws SQLException {
		String returnCols[] = { DataSource.COLUMN_STUDENTS_ID_STUDENT };
		PreparedStatement queryStudentByName = conn.prepareStatement(QUERY_STUDENT_BY_NAME);
		PreparedStatement insertStudent = conn.prepareStatement(INSERT_STUDENT, returnCols);

		int newID = 0;
		queryStudentByName.setString(1, firstName);
		queryStudentByName.setString(2, lastName);
		ResultSet results = queryStudentByName.executeQuery();
		if (!results.next()) {
			insertStudent.setString(1, firstName);
			insertStudent.setString(2, lastName);
			insertStudent.setString(3, phone);
			insertStudent.setDouble(4, rate);
			int affectedRows = insertStudent.executeUpdate();

			if (affectedRows != 1) {
				throw new SQLException("Couldn't insert student!");
			} else {
				ResultSet generatedKeys = insertStudent.getGeneratedKeys();
				if (generatedKeys.next()) {
					newID = generatedKeys.getInt(1);
				}
			}
		}
		queryStudentByName.close();
		insertStudent.close();

		return newID;
	}

	@Override
	public boolean updateStudent(String firstName, String lastName, String phone, double rate, int id)
			throws SQLException {
		PreparedStatement updateStudent = conn.prepareStatement(UPDATE_STUDENT);
		updateStudent.setString(1, firstName);
		updateStudent.setString(2, lastName);
		updateStudent.setString(3, phone);
		updateStudent.setDouble(4, rate);
		updateStudent.setInt(5, id);
		int affectedRows = updateStudent.executeUpdate();

		if (affectedRows != 1) {
			throw new SQLException("Couldn't update student!");
		}
		updateStudent.close();

		return true;
	}

	private boolean deleteStudent(int id) throws SQLException {
		PreparedStatement deleteStudent = conn.prepareStatement(DELETE_STUDENT);

		deleteStudent.setInt(1, id);
		int affectedRows = deleteStudent.executeUpdate();

		if (affectedRows != 1) {
			throw new SQLException("Couldn't delete student!");
		}
		deleteStudent.close();

		return true;
	}

	@Override
	public boolean deletePerson(Person person) throws SQLException {
		return deleteStudent(person.getPersonID());
	}

}
