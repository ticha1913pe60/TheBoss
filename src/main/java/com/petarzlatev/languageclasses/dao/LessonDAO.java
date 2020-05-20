package com.petarzlatev.languageclasses.dao;

import java.sql.SQLException;
import java.util.List;

import com.calendarfx.model.Entry;
import com.petarzlatev.languageclasses.model.Lesson;

public interface LessonDAO {

	/****************************************************
	 * CRUD *
	 ****************************************************/

	List<Lesson> loadLessons() throws SQLException;

	int addLesson(Entry<Lesson> entry) throws SQLException;

	boolean deleteLesson(int id) throws SQLException;

}