package com.petarzlatev.languageclasses.dao.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.calendarfx.model.Entry;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.dao.LessonDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Lesson;
import com.petarzlatev.languageclasses.model.Student;

public class SQLiteLessonDAO implements LessonDAO {
	private final Connection conn = DataSource.getInstance().getConnection();

	/****************************************************
	 * SQL *
	 ****************************************************/
	private static final String QUERY_LESSON_LIST = "SELECT " + DataSource.TABLE_LESSONS + "."
			+ DataSource.COLUMN_LESSONS_ID_LESSON + "," + DataSource.TABLE_LESSONS + "."
			+ DataSource.COLUMN_LESSONS_LANGUAGE + "," + DataSource.TABLE_LESSONS + "." + DataSource.COLUMN_LESSONS_DATE
			+ "," + DataSource.TABLE_LESSONS + "." + DataSource.COLUMN_LESSONS_START + "," + DataSource.TABLE_LESSONS
			+ "." + DataSource.COLUMN_LESSONS_END + "," + DataSource.TABLE_STUDENTS + "."
			+ DataSource.COLUMN_STUDENTS_ID_STUDENT + "," + DataSource.TABLE_STUDENTS + "."
			+ DataSource.COLUMN_STUDENTS_FIRSTNAME + "," + DataSource.TABLE_STUDENTS + "."
			+ DataSource.COLUMN_STUDENTS_LASTNAME + "," + DataSource.TABLE_STUDENTS + "."
			+ DataSource.COLUMN_STUDENTS_PHONE + "," + DataSource.TABLE_STUDENTS + "." + DataSource.COLUMN_STUDENTS_RATE
			+ " FROM " + DataSource.TABLE_LESSONS + " INNER JOIN " + DataSource.TABLE_STUDENTS + " ON "
			+ DataSource.TABLE_LESSONS + "." + DataSource.COLUMN_LESSONS_ID_STUDENT + "=" + DataSource.TABLE_STUDENTS
			+ "." + DataSource.COLUMN_STUDENTS_ID_STUDENT;

	private static final String INSERT_LESSON = "INSERT INTO " + DataSource.TABLE_LESSONS + " ( "
			+ DataSource.COLUMN_LESSONS_ID_STUDENT + ", " + DataSource.COLUMN_LESSONS_LANGUAGE + ", "
			+ DataSource.COLUMN_LESSONS_DATE + ",  " + DataSource.COLUMN_LESSONS_START + ",  "
			+ DataSource.COLUMN_LESSONS_END + " ) VALUES( ?, ?, ?, ?, ? )";

	private static final String UPDATE_LESSON = "UPDATE " + DataSource.TABLE_LESSONS + " SET "
			+ DataSource.COLUMN_LESSONS_ID_STUDENT + " = ?, " + DataSource.COLUMN_LESSONS_LANGUAGE + " = ?, "
			+ DataSource.COLUMN_LESSONS_DATE + " = ?, " + DataSource.COLUMN_LESSONS_START + " = ?, "
			+ DataSource.COLUMN_LESSONS_END + " = ? WHERE " + DataSource.COLUMN_LESSONS_ID_LESSON + " = ?";

	private static final String DELETE_LESSON = "DELETE FROM " + DataSource.TABLE_LESSONS + " WHERE "
			+ DataSource.COLUMN_LESSONS_ID_LESSON + " = ?";

	private static final String UPDATE_HIBERNATE = "UPDATE " + DataSource.TABLE_HIBERNATE_SEQ + " SET "
			+ DataSource.COLUMN_HIBERNATE_NEXT_VAL + " = ? WHERE " + DataSource.COLUMN_HIBERNATE_NEXT_VAL + " = ?";

	/****************************************************
	 * CRUD *
	 ****************************************************/

	@Override
	public List<Lesson> loadLessons() throws SQLException {
		PreparedStatement queryLessonList = conn.prepareStatement(QUERY_LESSON_LIST);

		ResultSet results = queryLessonList.executeQuery();
		List<Lesson> lessons = new ArrayList<Lesson>();
		while (results.next()) {
			Student student = new Student(results.getString(DataSource.COLUMN_STUDENTS_FIRSTNAME),
					results.getString(DataSource.COLUMN_STUDENTS_LASTNAME),
					results.getString(DataSource.COLUMN_STUDENTS_PHONE),
					results.getDouble(DataSource.COLUMN_STUDENTS_RATE),
					results.getInt(DataSource.COLUMN_STUDENTS_ID_STUDENT));

			Lesson lesson = new Lesson(student,
					Enum.valueOf(SystemLibrary.Language.class, results.getString(DataSource.COLUMN_LESSONS_LANGUAGE)),
					LocalDate.parse(results.getString(DataSource.COLUMN_LESSONS_DATE),
							DateTimeFormatter.ISO_LOCAL_DATE),
					LocalDateTime.parse(results.getString(DataSource.COLUMN_LESSONS_START)),
					LocalDateTime.parse(results.getString(DataSource.COLUMN_LESSONS_END)),
					results.getInt(DataSource.COLUMN_LESSONS_ID_LESSON));
			lessons.add(lesson);
		}
		queryLessonList.close();

		return lessons;
	}

	@Override
	public int addLesson(Entry<Lesson> entry) throws SQLException {
		PreparedStatement insertLesson = conn.prepareStatement(INSERT_LESSON, Statement.RETURN_GENERATED_KEYS);
		PreparedStatement updateLesson = conn.prepareStatement(UPDATE_LESSON);
		int newID = 0;

		if (entry.getUserObject() != null) {
			Lesson lesson = entry.getUserObject();

			if (lesson.getLessonID() > 0) {
				newID = lesson.getLessonID();
				updateLesson.setInt(1, lesson.getAtendingStudent().getPersonID());
				updateLesson.setString(2, lesson.getLangClassType().toString());
				updateLesson.setString(3, lesson.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
				updateLesson.setString(4, lesson.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				updateLesson.setString(5, lesson.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
				updateLesson.setInt(6, newID);
				int affectedRows = updateLesson.executeUpdate();

				if (affectedRows != 1) {
					throw new SQLException("Couldn't insert lesson!");
				}
			} else {
				if (lesson.getAtendingStudent() != null) {
					insertLesson.setInt(1, lesson.getAtendingStudent().getPersonID());
					insertLesson.setString(2, lesson.getLangClassType().toString());
					insertLesson.setString(3, lesson.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
					insertLesson.setString(4, lesson.getStart().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
					insertLesson.setString(5, lesson.getEnd().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
					int affectedRows = insertLesson.executeUpdate();

					if (affectedRows != 1) {
						throw new SQLException("Couldn't insert lesson!");
					} else {
						ResultSet generatedKeys = insertLesson.getGeneratedKeys();
						if (generatedKeys.next()) {
							newID = generatedKeys.getInt(1);
						}
						PreparedStatement updateHibernate = conn.prepareStatement(UPDATE_HIBERNATE);
						updateHibernate.setInt(1, newID + 1);
						updateHibernate.setInt(2, newID);
						affectedRows = updateHibernate.executeUpdate();
						updateHibernate.close();
					}
				}
			}
		}
		insertLesson.close();
		updateLesson.close();

		return newID;
	}

	@Override
	public boolean deleteLesson(int id) throws SQLException {
		PreparedStatement deleteLesson = conn.prepareStatement(DELETE_LESSON);

		deleteLesson.setInt(1, id);
		int affectedRows = deleteLesson.executeUpdate();

		if (affectedRows != 1) {
			throw new SQLException("Couldn't delete lesson!");
		}
		deleteLesson.close();

		return true;
	}
}
