package com.petarzlatev.languageclasses.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.dao.LessonDAO;
import com.petarzlatev.languageclasses.dao.StudentDAO;
import com.petarzlatev.languageclasses.dao.UserDAO;
import com.petarzlatev.languageclasses.dao.hibernate.HibernateLessonDAO;
import com.petarzlatev.languageclasses.dao.hibernate.HibernateStudentDAO;
import com.petarzlatev.languageclasses.dao.hibernate.HibernateUserDAO;
import com.petarzlatev.languageclasses.dao.oracle.OracleLessonDAO;
import com.petarzlatev.languageclasses.dao.oracle.OracleStudentDAO;
import com.petarzlatev.languageclasses.dao.oracle.OracleUserDAO;
import com.petarzlatev.languageclasses.dao.sqlite.SQLiteLessonDAO;
import com.petarzlatev.languageclasses.dao.sqlite.SQLiteStudentDAO;
import com.petarzlatev.languageclasses.dao.sqlite.SQLiteUserDAO;

public class DataSource {
	/****************************************************
	 * Constants *
	 ****************************************************/

	public static final String TABLE_USERS = "APP_USERS";
	public static final String COLUMN_USERS_ID_USER = "ID_PERSON";
	public static final String COLUMN_USERS_USERNAME = "USERNAME";
	public static final String COLUMN_USERS_PASSWORD = "PASSWORD";
	public static final String COLUMN_USERS_FIRSTNAME = "FIRSTNAME";
	public static final String COLUMN_USERS_LASTNAME = "LASTNAME";
	public static final String COLUMN_USERS_IS_ADMIN = "IS_ADMIN";
	public static final String COLUMN_USERS_SALT = "SALT";

	public static final String TABLE_STUDENTS = "APP_STUDENTS";
	public static final String COLUMN_STUDENTS_ID_STUDENT = "ID_PERSON";
	public static final String COLUMN_STUDENTS_FIRSTNAME = "FIRSTNAME";
	public static final String COLUMN_STUDENTS_LASTNAME = "LASTNAME";
	public static final String COLUMN_STUDENTS_PHONE = "PHONE";
	public static final String COLUMN_STUDENTS_RATE = "RATE";

	public static final String TABLE_LESSONS = "APP_LESSONS";
	public static final String COLUMN_LESSONS_ID_LESSON = "ID_LESSON";
	public static final String COLUMN_LESSONS_ID_STUDENT = "ID_STUDENT";
	public static final String COLUMN_LESSONS_LANGUAGE = "LANGUAGE_TYPE";
	public static final String COLUMN_LESSONS_DATE = "LESSON_DATE";
	public static final String COLUMN_LESSONS_START = "START_TIME";
	public static final String COLUMN_LESSONS_END = "END_TIME";

	public static final String TABLE_HIBERNATE_SEQ = "hibernate_sequence";
	public static final String COLUMN_HIBERNATE_NEXT_VAL = "next_val";

	public static final String SQLITE = "1";
	public static final String ORACLE = "2";

	private Connection conn;
	private static DataSource instance = new DataSource();

	private DataSource() {

	}

	public static DataSource getInstance() {
		return instance;
	}

	public Connection getConnection() {
		return conn;
	}

	public UserDAO getUserDAO() {
		String dbType = SystemLibrary.getProperty("DB_TYPE");
		UserDAO user = null;

		if (dbType != null) {
			if (SystemLibrary.useHibernate()) {
				user = new HibernateUserDAO();
			} else {
				if (dbType.equals(SQLITE)) {
					user = new SQLiteUserDAO();
				} else if (dbType.equals(ORACLE)) {
					user = new OracleUserDAO();
				}
			}
		}

		return user;
	}

	public StudentDAO getStudentDAO() {
		String dbType = SystemLibrary.getProperty("DB_TYPE");
		StudentDAO student = null;

		if (dbType != null) {
			if (SystemLibrary.useHibernate()) {
				student = new HibernateStudentDAO();
			} else {
				if (dbType.equals(SQLITE)) {
					student = new SQLiteStudentDAO();
				} else if (dbType.equals(ORACLE)) {
					student = new OracleStudentDAO();
				}
			}
		}

		return student;
	}

	public LessonDAO getLessonDAO() {
		String dbType = SystemLibrary.getProperty("DB_TYPE");
		LessonDAO lesson = null;

		if (dbType != null) {
			if (SystemLibrary.useHibernate()) {
				lesson = new HibernateLessonDAO();
			} else {
				if (dbType.equals(SQLITE)) {
					lesson = new SQLiteLessonDAO();
				} else if (dbType.equals(ORACLE)) {
					lesson = new OracleLessonDAO();
				}
			}
		}

		return lesson;
	}

	public static String getHibernateConfigFile() {
		String dbType = SystemLibrary.getProperty("DB_TYPE");
		String fileName = null;

		if (dbType != null) {
			if (dbType.equals(SQLITE)) {
				fileName = "hibernate_sqlite.cfg.xml";
			} else if (dbType.equals(ORACLE)) {
				fileName = "hibernate_oracle.cfg.xml";
			}
		}

		return fileName;
	}

	public static String getHibernateCreateConfigFile() {
		String dbType = SystemLibrary.getProperty("DB_TYPE");
		String fileName = null;

		if (dbType != null) {
			if (dbType.equals(SQLITE)) {
				fileName = "hibernate_sqlite_create.cfg.xml";
			} else if (dbType.equals(ORACLE)) {
				fileName = "hibernate_oracle_create.cfg.xml";
			}
		}

		return fileName;
	}

	/****************************************************
	 * Open DB *
	 ****************************************************/

	public boolean open() throws SQLException {
		boolean bRet = true;
		String dbDriver;
		String dbPath;
		String dbName;

		try {
			SystemLibrary.getSystemProperties();
			SystemLibrary.runLogger();
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (FileNotFoundException e) {
			System.out.println(Messages.getString("Error.ERROR_LOADING_CONFIG_FILE") + " " + e.getMessage());
			bRet = false;
		} catch (IOException e) {
			System.out.println(Messages.getString("Error.ERROR_IO_EXCEPTION") + " " + e.getMessage());
			bRet = false;
		} catch (ClassNotFoundException e) {
			System.out.println(Messages.getString("Error.ERROR_CLASS_NOT_FOUND") + " " + e.getMessage());
			bRet = false;
		}
		if (bRet) {
			String dbType = SystemLibrary.getProperty("DB_TYPE");
			if (dbType != null && (dbType.equals(SQLITE) || dbType.equals(ORACLE))) {
				if ((dbDriver = SystemLibrary.getProperty("DB_DRIVER")) != null) {
					if ((dbPath = SystemLibrary.getProperty("DB_PATH")) != null) {
						if ((dbName = SystemLibrary.getProperty("DB_NAME")) != null) {
							String username = SystemLibrary.getProperty("DB_USER");
							String password = SystemLibrary.getProperty("DB_PASS");
							if (dbType.equals(ORACLE)) {
								if (username != null) {
									if (password == null) {
										SystemLibrary.logEvent(
												Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_PASS",
												Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
										bRet = false;
									}
								} else {
									SystemLibrary.logEvent(
											Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_USER",
											Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
									bRet = false;
								}
							}
							if (bRet) {
								if (SystemLibrary.createDatabaseIfNotExist()) {
									if (!SystemLibrary.useHibernate()) {
										if (dbType.equals(ORACLE)) {
											conn = DriverManager.getConnection(dbDriver + dbPath + dbName, username,
													password);
										} else {
											conn = DriverManager.getConnection(dbDriver + dbPath + dbName);

										}
										SystemLibrary.logEvent("Established database connection:" + dbPath + dbName,
												Level.INFO, getClass().getName() + " " + SystemLibrary.methodName());
									}
								}
							}
						} else {
							SystemLibrary.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_NAME",
									Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
							bRet = false;
						}
					} else {
						SystemLibrary.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_PATH",
								Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
						bRet = false;
					}
				} else {
					SystemLibrary.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_DRIVER",
							Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
					bRet = false;
				}
			} else {
				SystemLibrary.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_TYPE", Level.SEVERE,
						getClass().getName() + " " + SystemLibrary.methodName());
				bRet = false;
			}
		}

		return bRet;
	}

	/****************************************************
	 * Close DB *
	 ****************************************************/

	public void close() throws SQLException {
		if (!SystemLibrary.useHibernate()) {
			if (conn != null) {
				conn.close();
			}
		}
	}

}
