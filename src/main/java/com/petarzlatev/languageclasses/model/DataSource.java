package com.petarzlatev.languageclasses.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.SystemLogger;
import com.petarzlatev.languageclasses.SystemProperties;
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

	private static final String SQLITE = "1";
	private static final String ORACLE = "2";

	/****************************************************
	 * Members *
	 ****************************************************/

	private Connection conn;

	private DataSource() {

	}

	private static class DataSourceHelper {
		private static DataSource instance = new DataSource();
	}

	public static DataSource getInstance() {
		return DataSourceHelper.instance;
	}

	public Connection getConnection() {
		return conn;
	}

	public UserDAO getUserDAO() {
		String dbType = SystemProperties.getDBType();
		UserDAO user = null;

		if (SystemProperties.useHibernate()) {
			user = new HibernateUserDAO();
		} else {
			if (dbType != null) {
				if (dbType.equals(SQLITE)) {
					user = new SQLiteUserDAO();
				} else if (dbType.equals(ORACLE)) {
					user = new OracleUserDAO();
				} else {
					SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM") + "DB_TYPE", Level.SEVERE,
							getClass().getName() + " " + SystemLibrary.methodName());
				}
			} else {
				SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_TYPE", Level.SEVERE,
						getClass().getName() + " " + SystemLibrary.methodName());
			}
		}

		return user;
	}

	public StudentDAO getStudentDAO() {
		String dbType = SystemProperties.getDBType();
		StudentDAO student = null;

		if (SystemProperties.useHibernate()) {
			student = new HibernateStudentDAO();
		} else {
			if (dbType != null) {
				if (dbType.equals(SQLITE)) {
					student = new SQLiteStudentDAO();
				} else if (dbType.equals(ORACLE)) {
					student = new OracleStudentDAO();
				} else {
					SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM") + "DB_TYPE", Level.SEVERE,
							getClass().getName() + " " + SystemLibrary.methodName());
				}
			} else {
				SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_TYPE", Level.SEVERE,
						getClass().getName() + " " + SystemLibrary.methodName());
			}
		}

		return student;
	}

	public LessonDAO getLessonDAO() {
		String dbType = SystemProperties.getDBType();
		LessonDAO lesson = null;

		if (SystemProperties.useHibernate()) {
			lesson = new HibernateLessonDAO();
		} else {
			if (dbType != null) {
				if (dbType.equals(SQLITE)) {
					lesson = new SQLiteLessonDAO();
				} else if (dbType.equals(ORACLE)) {
					lesson = new OracleLessonDAO();
				} else {
					SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM") + "DB_TYPE", Level.SEVERE,
							getClass().getName() + " " + SystemLibrary.methodName());
				}
			} else {
				SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_TYPE", Level.SEVERE,
						getClass().getName() + " " + SystemLibrary.methodName());
			}
		}

		return lesson;
	}

	public String getHibernateConfigFile() {
		String dbType = SystemProperties.getDBType();
		String fileName = null;

		if (dbType != null) {
			if (dbType.equals(SQLITE)) {
				fileName = "hibernate_sqlite.cfg.xml";
			} else if (dbType.equals(ORACLE)) {
				fileName = "hibernate_oracle.cfg.xml";
			} else {
				SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM") + "DB_TYPE", Level.SEVERE,
						getClass().getName() + " " + SystemLibrary.methodName());
			}
		} else {
			SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_TYPE", Level.SEVERE,
					getClass().getName() + " " + SystemLibrary.methodName());
		}

		return fileName;
	}

	public String getHibernateCreateConfigFile() {
		String dbType = SystemProperties.getDBType();
		String fileName = null;

		if (dbType != null) {
			if (dbType.equals(SQLITE)) {
				fileName = "hibernate_sqlite_create.cfg.xml";
			} else if (dbType.equals(ORACLE)) {
				fileName = "hibernate_oracle_create.cfg.xml";

			} else {
				SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM") + "DB_TYPE", Level.SEVERE,
						getClass().getName() + " " + SystemLibrary.methodName());
			}
		} else {
			SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_TYPE", Level.SEVERE,
					getClass().getName() + " " + SystemLibrary.methodName());
		}

		return fileName;
	}

	/****************************************************
	 * Create DB *
	 ****************************************************/
	public static boolean createDatabaseIfNotExist() {

		if (SystemProperties.createDatabase()) {
			SessionFactory factory = new Configuration().configure(getInstance().getHibernateCreateConfigFile())
					.addAnnotatedClass(Lesson.class).addAnnotatedClass(Student.class).buildSessionFactory();
			Session session = factory.getCurrentSession();

			try {
				session.beginTransaction();
				User defaultUser = new User("Kristina", "Paskulova", "pakri",
						"8VoeSn7gIjWB+/espWp2we4CCkEzzd5yua0QEj3jQhY=", "T", 0, "6fhUbVwn7b5EjmOjrA0UFvMU1nTH9L");
				session.save(defaultUser);
				session.getTransaction().commit();

				return true;
			} finally {
				session.close();
				factory.close();
			}
		}

		return true;
	}

	/****************************************************
	 * Open DB *
	 ****************************************************/

	public boolean open() throws SQLException {
		boolean bRet = true;
		String dbDriver;
		String dbPath;
		String dbName;
		String dbType;
		String username = null;
		String password = null;

		try {
			SystemProperties.getSystemProperties();
			SystemLogger.runLogger();
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
			if ((dbType = SystemProperties.getDBType()) != null) {
				if (dbType.equals(SQLITE) || dbType.equals(ORACLE)) {
					if ((dbDriver = SystemProperties.getDBDriver()) != null) {
						if ((dbPath = SystemProperties.getDBPath()) != null) {
							if ((dbName = SystemProperties.getDBName()) != null) {
								if (dbType.equals(ORACLE)) {
									if ((username = SystemProperties.getOracleUserName()) != null) {
										if ((password = SystemProperties.getOraclePassWord()) == null) {
											SystemLogger.logEvent(
													Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_PASS",
													Level.SEVERE,
													getClass().getName() + " " + SystemLibrary.methodName());
											bRet = false;
										}
									} else {
										SystemLogger.logEvent(
												Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_USER",
												Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
										bRet = false;
									}
								}
								if (bRet) {
									if (DataSource.createDatabaseIfNotExist()) {
										if (!SystemProperties.useHibernate()) {
											if (dbType.equals(ORACLE)) {
												conn = DriverManager.getConnection(dbDriver + dbPath + dbName, username,
														password);
											} else {
												conn = DriverManager.getConnection(dbDriver + dbPath + dbName);

											}
											SystemLogger.logEvent("Established database connection:" + dbPath + dbName,
													Level.INFO,
													getClass().getName() + " " + SystemLibrary.methodName());
										}
									}
								}
							} else {
								SystemLogger.logEvent(
										Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_NAME",
										Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
								bRet = false;
							}
						} else {
							SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_PATH",
									Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
							bRet = false;
						}
					} else {
						SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_DRIVER",
								Level.SEVERE, getClass().getName() + " " + SystemLibrary.methodName());
						bRet = false;
					}
				} else {
					SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM") + "DB_TYPE", Level.SEVERE,
							getClass().getName() + " " + SystemLibrary.methodName());
					bRet = false;
				}
			} else {
				SystemLogger.logEvent(Messages.getString("Error.ERROR_CONFIG_PARAM_MISSING") + "DB_TYPE", Level.SEVERE,
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
		if (!SystemProperties.useHibernate()) {
			if (conn != null) {
				conn.close();
			}
		}
	}

}
