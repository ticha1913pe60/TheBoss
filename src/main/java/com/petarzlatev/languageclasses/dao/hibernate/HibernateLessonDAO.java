package com.petarzlatev.languageclasses.dao.hibernate;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.calendarfx.model.Entry;
import com.petarzlatev.languageclasses.dao.LessonDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Lesson;
import com.petarzlatev.languageclasses.model.Student;

public class HibernateLessonDAO implements LessonDAO {

	@Override
	public List<Lesson> loadLessons() throws SQLException {
		SessionFactory factory = new Configuration().configure(DataSource.getHibernateConfigFile())
				.addAnnotatedClass(Lesson.class).addAnnotatedClass(Student.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			@SuppressWarnings("unchecked")
			List<Lesson> lessons = session.createQuery("from Lesson").getResultList();
			session.getTransaction().commit();

			return lessons;
		} finally {
			session.close();
			factory.close();
		}

	}

	@Override
	public int addLesson(Entry<Lesson> entry) throws SQLException {
		int newID = 0;
		SessionFactory factory = new Configuration().configure(DataSource.getHibernateConfigFile())
				.addAnnotatedClass(Student.class).addAnnotatedClass(Lesson.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			if (entry.getUserObject() != null) {
				Lesson lesson = entry.getUserObject();

				if (lesson.getLessonID() > 0) {
					newID = lesson.getLessonID();
					session.beginTransaction();
					session.merge(lesson);
					session.getTransaction().commit();
				} else {
					System.out.println(entry.toString());
					session.beginTransaction();
					Student atending = entry.getUserObject().getAtendingStudent();
					lesson = new Lesson(atending, entry.getUserObject().getLangClassType(),
							entry.getUserObject().getDate(), entry.getUserObject().getStart(),
							entry.getUserObject().getEnd(), 0);
					newID = (int) session.save(lesson);
					session.getTransaction().commit();
				}
			}

			return newID;
		} finally {
			session.close();
			factory.close();
		}

	}

	@Override
	public boolean deleteLesson(int id) throws SQLException {
		SessionFactory factory = new Configuration().configure(DataSource.getHibernateConfigFile())
				.addAnnotatedClass(Student.class).addAnnotatedClass(Lesson.class).buildSessionFactory();
		Session session = factory.getCurrentSession();

		try {
			session.beginTransaction();
			Query myQuery = session.createQuery("delete from Lesson where ID_LESSON=:id").setParameter("id", id);
			int count = myQuery.executeUpdate();
			session.getTransaction().commit();

			return count == 1;
		} finally {
			session.close();
			factory.close();
		}
	}

}
