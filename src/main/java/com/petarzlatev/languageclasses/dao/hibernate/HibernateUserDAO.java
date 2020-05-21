package com.petarzlatev.languageclasses.dao.hibernate;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.petarzlatev.languageclasses.dao.PersonDAO;
import com.petarzlatev.languageclasses.dao.UserDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Person;
import com.petarzlatev.languageclasses.model.Student;
import com.petarzlatev.languageclasses.model.User;

public class HibernateUserDAO implements PersonDAO, UserDAO {
	@Override
	public User loadCurrentUser(String username) throws SQLException {
		SessionFactory factory = new Configuration().configure(DataSource.getInstance().getHibernateConfigFile())
				.addAnnotatedClass(User.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			Query myQuery = session.createQuery("from User where userName=:user").setParameter("user", username);
			@SuppressWarnings("unchecked")
			List<User> users = myQuery.getResultList();
			User loginUser = null;
			if (users != null && users.size() > 0) {
				loginUser = users.get(0);
			}
			session.getTransaction().commit();

			return loginUser;
		} finally {
			session.close();
			factory.close();
		}

	}

	@Override
	public List<User> loadUsers() throws SQLException {
		SessionFactory factory = new Configuration().configure(DataSource.getInstance().getHibernateConfigFile())
				.addAnnotatedClass(User.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<User> users = session.createQuery("from User").getResultList();
			session.getTransaction().commit();

			return users;
		} finally {
			session.close();
			factory.close();
		}
	}

	@Override
	public int addUser(String firstName, String lastName, String username, String password, String isAdmin, String salt)
			throws SQLException {
		int newID = 0;
		SessionFactory factory = new Configuration().configure(DataSource.getInstance().getHibernateConfigFile())
				.addAnnotatedClass(User.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			Query myQuery = session.createQuery("from User where userName=:uname");
			myQuery.setParameter("uname", username);
			@SuppressWarnings("unchecked")
			List<User> users = myQuery.getResultList();
			session.getTransaction().commit();

			if (users != null && users.size() > 0) {
				return 0;
			} else {
				session = factory.getCurrentSession();
				User newUser = new User(firstName, lastName, username, password, isAdmin, 0, salt);

				session.beginTransaction();
				newID = (int) session.save(newUser);
				session.getTransaction().commit();

				return newID;
			}
		} finally {
			session.close();
			factory.close();
		}

	}

	@Override
	public boolean updateUser(String firstName, String lastName, String username, String password, String isAdmin,
			int id) throws SQLException {
		SessionFactory factory = new Configuration().configure(DataSource.getInstance().getHibernateConfigFile())
				.addAnnotatedClass(User.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			User user = session.get(User.class, id);

			if (user != null) {
				user.setFirstName(firstName);
				user.setLastName(lastName);
				user.setIsAdmin(isAdmin);
				user.setUsername(username);
				user.setPassword(password);
			}

			session.getTransaction().commit();
		} finally {
			session.close();
			factory.close();
		}

		return true;
	}

	@Override
	public boolean deletePerson(Person person) throws SQLException {
		SessionFactory factory = new Configuration().configure(DataSource.getInstance().getHibernateConfigFile())
				.addAnnotatedClass(Student.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			Query myQuery = session.createQuery("delete from User where ID_PERSON=:id").setParameter("id",
					person.getPersonID());
			int count = myQuery.executeUpdate();
			session.getTransaction().commit();

			return count == 1;
		} finally {
			session.close();
			factory.close();
		}
	}

}
