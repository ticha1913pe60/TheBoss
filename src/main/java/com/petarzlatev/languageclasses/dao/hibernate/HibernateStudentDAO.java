package com.petarzlatev.languageclasses.dao.hibernate;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.petarzlatev.languageclasses.dao.PersonDAO;
import com.petarzlatev.languageclasses.dao.StudentDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Person;
import com.petarzlatev.languageclasses.model.Student;

public class HibernateStudentDAO implements PersonDAO, StudentDAO {
	@Override
	public List<Student> loadStudents() throws SQLException {
		SessionFactory factory = new Configuration().configure(DataSource.getHibernateConfigFile())
				.addAnnotatedClass(Student.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Student> students = session.createQuery("from Student").getResultList();
			session.getTransaction().commit();

			return students;
		} finally {
			session.close();
			factory.close();
		}
	}

	@Override
	public int addStudent(String firstName, String lastName, String phone, double rate) throws SQLException {
		int newID = 0;
		SessionFactory factory = new Configuration().configure(DataSource.getHibernateConfigFile())
				.addAnnotatedClass(Student.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			Query myQuery = session.createQuery("from Student where firstName=:fname and lastName=:lname");
			myQuery.setParameter("fname", firstName);
			myQuery.setParameter("lname", lastName);
			@SuppressWarnings("unchecked")
			List<Student> students = myQuery.getResultList();
			session.getTransaction().commit();

			if (students != null && students.size() > 0) {
				return 0;
			} else {
				session = factory.getCurrentSession();
				Student newStudent = new Student(firstName, lastName, phone, rate, 0);

				session.beginTransaction();
				newID = (int) session.save(newStudent);
				session.getTransaction().commit();
				
				return newID;
			}
		} finally {
			session.close();
			factory.close();
		}

	}

	@Override
	public boolean updateStudent(String firstName, String lastName, String phone, double rate, int id)
			throws SQLException {
		SessionFactory factory = new Configuration().configure(DataSource.getHibernateConfigFile())
				.addAnnotatedClass(Student.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			Student student = session.get(Student.class, id);

			if (student != null) {
				student.setFirstName(firstName);
				student.setLastName(lastName);
				student.setPhoneNumber(phone);
				student.setRatePerHour(rate);
			}

			session.getTransaction().commit();
		} finally {
			session.close();
			factory.close();
		}

		return true;
	}

	private boolean deleteStudent(int id) throws SQLException {
		SessionFactory factory = new Configuration().configure(DataSource.getHibernateConfigFile())
				.addAnnotatedClass(Student.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			Student student = session.get(Student.class, id);
			session.delete(student);
			session.getTransaction().commit();

			return true;
		} finally {
			session.close();
			factory.close();
		}
	}

	@Override
	public boolean deletePerson(Person person) throws SQLException {
		return deleteStudent(person.getPersonID());
	}

}
