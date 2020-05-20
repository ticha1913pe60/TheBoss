package com.petarzlatev.languageclasses.view;

import java.sql.SQLException;
import java.util.Objects;

import org.controlsfx.control.PopOver;

import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.popover.EntryHeaderView;
import com.calendarfx.view.popover.EntryPropertiesView;
import com.calendarfx.view.popover.PopOverContentPane;
import com.calendarfx.view.popover.PopOverTitledPane;
import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.dao.LessonDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Lesson;

import javafx.beans.InvalidationListener;
import javafx.util.Duration;

public class LessonPopOverContentPane extends PopOverContentPane {
	private Entry<Lesson> entry;
	private DateControl dateControl;
	private PopOver popOver;

	public LessonPopOverContentPane(PopOver popOver, DateControl dateControl, Entry<Lesson> entry) {
		getStylesheets().add(CalendarView.class.getResource("calendar.css").toExternalForm()); //$NON-NLS-1$

		this.popOver = popOver;
		this.dateControl = dateControl;
		this.entry = Objects.requireNonNull(entry);

		LessonDetailsView details = new LessonDetailsView(entry);

		PopOverTitledPane detailsPane = new PopOverTitledPane(Messages.getString("EntryPopOverContentPane.DETAILS"), //$NON-NLS-1$
				details);

		EntryHeaderView header = new EntryHeaderView(entry, dateControl.getCalendars());
		setHeader(header);

		if (Boolean.getBoolean("calendarfx.developer")) {
			EntryPropertiesView properties = new EntryPropertiesView(entry);
			PopOverTitledPane propertiesPane = new PopOverTitledPane("Properties", properties);
			getPanes().addAll(detailsPane, propertiesPane);
		} else {
			getPanes().addAll(detailsPane);
		}

		setExpandedPane(detailsPane);

		InvalidationListener listener = obs -> {
			if (entry.isFullDay() && !popOver.isDetached()) {
				popOver.setDetached(true);
			}
		};

		entry.fullDayProperty().addListener(listener);
		popOver.setOnHidden(evt -> {
			entry.fullDayProperty().removeListener(listener);
			try {
				LessonDAO database = DataSource.getInstance().getLessonDAO();
				int lessonID = database.addLesson(entry);
				if(lessonID > 0) {
					entry.getUserObject().setLessonID(lessonID);
				}
			} catch (SQLException e) {
				System.out.println(Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage());
			}
		});

		entry.calendarProperty().addListener(it -> {
			if (entry.getCalendar() == null) {
				popOver.hide(Duration.ZERO);
			}
		});
	}

	public final PopOver getPopOver() {
		return popOver;
	}

	public final DateControl getDateControl() {
		return dateControl;
	}

	public final Entry<Lesson> getEntry() {
		return entry;
	}
}
