package com.petarzlatev.languageclasses.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.dao.hibernate.LocalDateConverter;
import com.petarzlatev.languageclasses.dao.hibernate.LocalDateTimeConverter;

@Entity
@Table(name = "APP_LESSONS")
public class Lesson {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "ID_LESSON")
	private int lessonID;
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Student.class)
	@JoinColumn(name = "ID_STUDENT", referencedColumnName = "ID_PERSON")
	private Student atendingStudent;
	@Enumerated(EnumType.STRING)
	@Column(name = "LANGUAGE_TYPE")
	private SystemLibrary.Language langClassType;
	@Column(name = "LESSON_DATE")
	@Convert(converter = LocalDateConverter.class)
	private LocalDate date;
	@Column(name = "START_TIME")
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime start;
	@Column(name = "END_TIME")
	@Convert(converter = LocalDateTimeConverter.class)
	private LocalDateTime end;
	
	public Lesson() {
		
	}

	public Lesson(Student atendingStudent, SystemLibrary.Language langClassType, LocalDate date, LocalDateTime start,
			LocalDateTime end, int lessonid) {
		this.atendingStudent = atendingStudent;
		this.langClassType = langClassType;
		this.lessonID = lessonid;
		this.date = date;
		this.start = start;
		this.end = end;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public int getLessonID() {
		return lessonID;
	}

	public void setLessonID(int lessonID) {
		this.lessonID = lessonID;
	}

	public Student getAtendingStudent() {
		return atendingStudent;
	}

	public void setAtendingStudent(Student atendingStudent) {
		this.atendingStudent = atendingStudent;
	}

	public SystemLibrary.Language getLangClassType() {
		return langClassType;
	}

	public void setLangClassType(SystemLibrary.Language langClassType) {
		this.langClassType = langClassType;
	}

	public String getClassType() {
		return langClassType.getName();
	}
}
