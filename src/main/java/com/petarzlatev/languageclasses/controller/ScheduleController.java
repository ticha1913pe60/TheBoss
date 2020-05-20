package com.petarzlatev.languageclasses.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.LocalTime;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Calendar.Style;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.DateControl.EditOperation;
import com.calendarfx.view.DateControl.EntryDetailsParameter;
import com.calendarfx.view.DateControl.EntryEditParameter;
import com.calendarfx.view.EntryViewBase;
import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.dao.LessonDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Lesson;
import com.petarzlatev.languageclasses.view.LessonCalendarView;
import com.petarzlatev.languageclasses.view.LessonPopOverContentPane;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;

public class ScheduleController extends Controller implements Initializable {
	private final LessonDAO database = DataSource.getInstance().getLessonDAO();
	@FXML
	private Pane thePane;
	@FXML
	private LessonCalendarView calendarView;
	@FXML
	private Menu menuOptions;
	@FXML
	private MenuItem menuItemMain;
	@FXML
	private MenuItem menuItemUsers;
	@FXML
	private MenuItem menuItemStudents;
	@FXML
	private MenuItem menuItemExit;
	@FXML
	private MenuItem menuItemLogOut;

	/****************************************************
	 * Initializable
	 ****************************************************/
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menuOptions.setText(Messages.getString("MenuItem.OPTIONS"));
		menuItemMain.setText(Messages.getString("MenuItem.MAIN_MENU"));
		menuItemUsers.setText(Messages.getString("MenuItem.USERS"));
		menuItemStudents.setText(Messages.getString("MenuItem.STUDENTS"));
		menuItemExit.setText(Messages.getString("MenuItem.EXIT"));
		menuItemLogOut.setText(Messages.getString("MenuItem.LOG_OUT"));

		menuItemLogOut.setDisable(SystemLibrary.noLogin());
		Calendar lessons = new Calendar("Lessons");

		calendarView.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				handleMainMenu();
			}
		});
		calendarView.setEntryContextMenuCallback(param -> {
			EntryViewBase<CalendarView> entryView = (EntryViewBase<CalendarView>) param.getEntryView();
			Entry<Lesson> entry = (Entry<Lesson>) entryView.getEntry();

			ContextMenu contextMenu = new ContextMenu();

			/*
			 * Show dialog / popover with entry details.
			 */
			MenuItem informationItem = new MenuItem(Messages.getString("DateControl.MENU_ITEM_INFORMATION")); //$NON-NLS-1$
			informationItem.setOnAction(evt -> {
				Callback<EntryDetailsParameter, Boolean> detailsCallback = calendarView.getEntryDetailsCallback();
				if (detailsCallback != null) {
					ContextMenuEvent ctxEvent = param.getContextMenuEvent();
					EntryDetailsParameter entryDetailsParam = new EntryDetailsParameter(ctxEvent, calendarView,
							entryView.getEntry(), calendarView, ctxEvent.getScreenX(), ctxEvent.getScreenY());
					detailsCallback.call(entryDetailsParam);
				}
			});
			contextMenu.getItems().add(informationItem);

			String stylesheet = CalendarView.class.getResource("calendar.css") //$NON-NLS-1$
					.toExternalForm();

			/*
			 * Assign entry to different calendars.
			 */
			Menu calendarMenu = new Menu(Messages.getString("DateControl.MENU_CALENDAR")); //$NON-NLS-1$
			for (Calendar calendar : calendarView.getCalendars()) {
				RadioMenuItem calendarItem = new RadioMenuItem(calendar.getName());
				calendarItem.setOnAction(evt -> entry.setCalendar(calendar));
				calendarItem.setDisable(calendar.isReadOnly());
				calendarItem.setSelected(calendar.equals(param.getCalendar()));
				calendarMenu.getItems().add(calendarItem);

				StackPane graphic = new StackPane();
				graphic.getStylesheets().add(stylesheet);

				/*
				 * Icon has to be wrapped in a stackpane so that a stylesheet can be added to
				 * it.
				 */
				Rectangle icon = new Rectangle(10, 10);
				icon.setArcHeight(2);
				icon.setArcWidth(2);
				icon.getStyleClass().setAll(calendar.getStyle() + "-icon"); //$NON-NLS-1$
				graphic.getChildren().add(icon);

				calendarItem.setGraphic(graphic);
			}

			calendarMenu.setDisable(param.getCalendar().isReadOnly());
			contextMenu.getItems().add(calendarMenu);

			if (calendarView.getEntryEditPolicy()
					.call(new EntryEditParameter(calendarView, entry, EditOperation.DELETE))) {
				/*
				 * Delete calendar entry.
				 */
				MenuItem delete = new MenuItem(Messages.getString("DateControl.MENU_ITEM_DELETE")); //$NON-NLS-1$
				contextMenu.getItems().add(delete);
				delete.setDisable(param.getCalendar().isReadOnly());
				delete.setOnAction(evt -> {
					Calendar calendar = entry.getCalendar();
					if (!calendar.isReadOnly()) {
						if (entry.isRecurrence()) {
							entry.getRecurrenceSourceEntry().removeFromCalendar();
						} else {
							entry.removeFromCalendar();
						}
						try {
							if (entry.getUserObject() != null && entry.getUserObject().getLessonID() > 0) {
								database.deleteLesson(entry.getUserObject().getLessonID());
							}
						} catch (SQLException e1) {
							SystemLibrary.showErrorMsg(
									Messages.getString("Error.ERROR_SQL_QUERY") + " " + e1.getMessage(),
									Messages.getString("System.ERROR"));
						}
					}
				});
			}

			return contextMenu;
		});

		lessons.setReadOnly(!SystemLibrary.getCurrentUser().isAdmin());

		Task<List<Lesson>> task = new Task<List<Lesson>>() {

			@Override
			protected List<Lesson> call() throws Exception {
				return database.loadLessons();
			}
		};

		new Thread(task).start();

		task.setOnSucceeded(e -> {
			for (Lesson lesson : task.getValue()) {
				Entry<Lesson> entry = new Entry<Lesson>();
				entry.setUserObject(lesson);
				entry.setTitle(Messages.getString("EntryDetailsView.STUDENT") + " "
						+ entry.getUserObject().getAtendingStudent().getFullName() + " \n"
						+ Messages.getString("EntryDetailsView.LANGUAGE") + " " + entry.getUserObject().getClassType());
				entry.changeStartDate(lesson.getDate());
				entry.changeEndDate(lesson.getDate());
				entry.changeStartTime(lesson.getStart().toLocalTime());
				entry.changeEndTime(lesson.getEnd().toLocalTime());
				entry.setCalendar(lessons);
			}
			try {
				lessons.setStyle(Style.valueOf("STYLE" + SystemLibrary.getProperty("STYLE")));
			} catch (IllegalArgumentException e1) {
			} catch (NullPointerException e2) {
			}

			CalendarSource myCalendarSource = new CalendarSource("My Calendars");
			myCalendarSource.getCalendars().addAll(lessons);

			calendarView.getCalendarSources().clear();
			calendarView.getCalendarSources().add(myCalendarSource);
			calendarView.setEntryDetailsPopOverContentCallback(param -> new LessonPopOverContentPane(param.getPopOver(),
					param.getDateControl(), (Entry<Lesson>) param.getEntry()));

			calendarView.setRequestedTime(LocalTime.now());

			Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
				@Override
				public void run() {
					while (true) {
						Platform.runLater(() -> {
							calendarView.setToday(LocalDate.now());
							calendarView.setTime(LocalTime.now());
						});

						try {
							// update every 10 seconds
							sleep(10000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					}
				};
			};

			updateTimeThread.setPriority(Thread.MIN_PRIORITY);
			updateTimeThread.setDaemon(true);
			updateTimeThread.start();
		});
	}
}
