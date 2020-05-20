package com.petarzlatev.languageclasses.view;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

import com.calendarfx.model.Entry;
import com.calendarfx.util.Util;
import com.calendarfx.view.RecurrenceView;
import com.calendarfx.view.TimeField;
import com.calendarfx.view.popover.EntryPopOverPane;
import com.calendarfx.view.popover.RecurrencePopup;
import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.SystemLibrary;
import com.petarzlatev.languageclasses.dao.StudentDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Lesson;
import com.petarzlatev.languageclasses.model.Student;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

public class LessonDetailsView extends EntryPopOverPane {

    private final Label summaryLabel;
    private final MenuButton recurrenceButton;

    public LessonDetailsView(Entry<Lesson> entry) {
        super();

        List<Student> studentsList = null;
        StudentDAO database = DataSource.getInstance().getStudentDAO();

        try {
            studentsList = database.loadStudents();
        } catch (SQLException e) {
            SystemLibrary.showErrorMsg(Messages.getString("Error.ERROR_SQL_QUERY") + " " + e.getMessage(),
                    Messages.getString("System.ERROR"));
        }
        getStyleClass().add("entry-details-view");

        Label fullDayLabel = new Label(Messages.getString("EntryDetailsView.FULL_DAY")); //$NON-NLS-1$
        Label startDateLabel = new Label(Messages.getString("EntryDetailsView.FROM")); //$NON-NLS-1$
        Label endDateLabel = new Label(Messages.getString("EntryDetailsView.TO")); //$NON-NLS-1$
        Label recurrentLabel = new Label(Messages.getString("EntryDetailsView.REPEAT")); //$NON-NLS-1$

        summaryLabel = new Label();
        summaryLabel.getStyleClass().add("recurrence-summary-label"); //$NON-NLS-1$
        summaryLabel.setWrapText(true);
        summaryLabel.setMaxWidth(300);

        CheckBox fullDay = new CheckBox();
        fullDay.disableProperty().bind(entry.getCalendar().readOnlyProperty());

        TimeField startTimeField = new TimeField();
        startTimeField.setValue(entry.getStartTime());
        startTimeField.disableProperty().bind(entry.getCalendar().readOnlyProperty());

        TimeField endTimeField = new TimeField();
        endTimeField.setValue(entry.getEndTime());
        endTimeField.disableProperty().bind(entry.getCalendar().readOnlyProperty());

        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setValue(entry.getStartDate());
        startDatePicker.disableProperty().bind(entry.getCalendar().readOnlyProperty());

        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setValue(entry.getEndDate());
        endDatePicker.disableProperty().bind(entry.getCalendar().readOnlyProperty());

        entry.intervalProperty().addListener(it -> {
            startTimeField.setValue(entry.getStartTime());
            endTimeField.setValue(entry.getEndTime());
            startDatePicker.setValue(entry.getStartDate());
            endDatePicker.setValue(entry.getEndDate());
        });

        HBox startDateBox = new HBox(10);
        HBox endDateBox = new HBox(10);

        startDateBox.setAlignment(Pos.CENTER_LEFT);
        endDateBox.setAlignment(Pos.CENTER_LEFT);

        startDateBox.getChildren().addAll(startDateLabel, startDatePicker, startTimeField);
        endDateBox.getChildren().addAll(endDateLabel, endDatePicker, endTimeField);

        fullDay.setSelected(entry.isFullDay());
        startDatePicker.setValue(entry.getStartDate());
        endDatePicker.setValue(entry.getEndDate());

        ObservableList<Student> students = FXCollections.observableArrayList(studentsList);

        students.sort(Comparator.comparing(Student::getFirstName));

        Label studentLabel = new Label(Messages.getString("EntryDetailsView.STUDENT")); //$NON-NLS-1$

        ComboBox<Student> studentBox = new ComboBox<>(students);
        studentBox.disableProperty().bind(entry.getCalendar().readOnlyProperty());
        studentBox.setConverter(new StringConverter<Student>() {

            @Override
            public String toString(Student student) {
                return student.getFullName();
            }

            @Override
            public Student fromString(String string) {
                return null;
            }
        });

        Label languageLabel = new Label(Messages.getString("EntryDetailsView.LANGUAGE")); //$NON-NLS-1$

        ComboBox<SystemLibrary.Language> languageBox = new ComboBox<>();
        languageBox.getItems().setAll(SystemLibrary.Language.values());
        languageBox.disableProperty().bind(entry.getCalendar().readOnlyProperty());
        languageBox.setConverter(new StringConverter<SystemLibrary.Language>() {

            @Override
            public String toString(SystemLibrary.Language langType) {
                return langType.getName();
            }

            @Override
            public SystemLibrary.Language fromString(String string) {
                return null;
            }
        });

        if (entry.getUserObject() != null) {
            studentBox.setValue(entry.getUserObject().getAtendingStudent());
            languageBox.setValue(entry.getUserObject().getLangClassType());
        }

        recurrenceButton = new MenuButton(Messages.getString("EntryDetailsView.MENU_BUTTON_NONE")); //$NON-NLS-1$

        MenuItem none = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_NONE")); //$NON-NLS-1$
        MenuItem everyDay = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_EVERY_DAY")); //$NON-NLS-1$
        MenuItem everyWeek = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_EVERY_WEEK")); //$NON-NLS-1$
        MenuItem everyMonth = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_EVERY_MONTH")); //$NON-NLS-1$
        MenuItem everyYear = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_EVERY_YEAR")); //$NON-NLS-1$
        MenuItem custom = new MenuItem(Messages.getString("EntryDetailsView.MENU_ITEM_CUSTOM")); //$NON-NLS-1$

        none.setOnAction(evt -> updateRecurrenceRule(entry, null));
        everyDay.setOnAction(evt -> updateRecurrenceRule(entry, "RRULE:FREQ=DAILY")); //$NON-NLS-1$
        everyWeek.setOnAction(evt -> updateRecurrenceRule(entry, "RRULE:FREQ=WEEKLY")); //$NON-NLS-1$
        everyMonth.setOnAction(evt -> updateRecurrenceRule(entry, "RRULE:FREQ=MONTHLY")); //$NON-NLS-1$
        everyYear.setOnAction(evt -> updateRecurrenceRule(entry, "RRULE:FREQ=YEARLY")); //$NON-NLS-1$
        custom.setOnAction(evt -> showRecurrenceEditor(entry));

        recurrenceButton.getItems().setAll(none, everyDay, everyWeek, everyMonth, everyYear, new SeparatorMenuItem(),
                custom);
        recurrenceButton.disableProperty().bind(entry.getCalendar().readOnlyProperty());

        GridPane box = new GridPane();
        box.getStyleClass().add("content"); //$NON-NLS-1$
        box.add(fullDayLabel, 0, 0);
        box.add(fullDay, 1, 0);
        box.add(startDateLabel, 0, 1);
        box.add(startDateBox, 1, 1);
        box.add(endDateLabel, 0, 2);
        box.add(endDateBox, 1, 2);
        box.add(studentLabel, 0, 3);
        box.add(studentBox, 1, 3);
        box.add(languageLabel, 0, 4);
        box.add(languageBox, 1, 4);
        box.add(recurrentLabel, 0, 5);
        box.add(recurrenceButton, 1, 5);
        box.add(summaryLabel, 1, 6);

        GridPane.setFillWidth(studentBox, true);
        GridPane.setHgrow(studentBox, Priority.ALWAYS);

        GridPane.setFillWidth(languageBox, true);
        GridPane.setHgrow(languageBox, Priority.ALWAYS);

        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();

        col1.setHalignment(HPos.RIGHT);
        col2.setHalignment(HPos.LEFT);

        box.getColumnConstraints().addAll(col1, col2);

        getChildren().add(box);

        startTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));
        endTimeField.visibleProperty().bind(Bindings.not(entry.fullDayProperty()));

        // start date and time
        startDatePicker.valueProperty().addListener(evt -> {
            if (entry.getUserObject() == null) {
                Lesson less = new Lesson(studentBox.getValue(), languageBox.getValue(), startDatePicker.getValue(),
                        startDatePicker.getValue().atTime(entry.getStartTime()),
                        startDatePicker.getValue().atTime(entry.getEndTime()), 0);
                entry.setUserObject(less);
            }
            entry.changeStartDate(startDatePicker.getValue());
            entry.getUserObject().setDate(startDatePicker.getValue());
        });
        startTimeField.valueProperty().addListener(evt -> {
            if (entry.getUserObject() == null) {
                Lesson less = new Lesson(studentBox.getValue(), languageBox.getValue(), entry.getStartDate(),
                        entry.getStartDate().atTime(entry.getStartTime()),
                        entry.getStartDate().atTime(entry.getEndTime()), 0);
                entry.setUserObject(less);
            }
            entry.changeStartTime(startTimeField.getValue(), true);
            entry.getUserObject().setStart(entry.getStartDate().atTime(startTimeField.getValue()));
        });

        // end date and time
        endDatePicker.valueProperty().addListener(evt -> {
            if (entry.getUserObject() == null) {
                Lesson less = new Lesson(studentBox.getValue(), languageBox.getValue(), entry.getStartDate(),
                        entry.getStartDate().atTime(entry.getStartTime()),
                        entry.getStartDate().atTime(entry.getEndTime()), 0);
                entry.setUserObject(less);
            }
            entry.changeEndDate(endDatePicker.getValue());
            entry.getUserObject().setDate(endDatePicker.getValue());
        });
        endTimeField.valueProperty().addListener(evt -> {
            if (entry.getUserObject() == null) {
                Lesson less = new Lesson(studentBox.getValue(), languageBox.getValue(), entry.getStartDate(),
                        entry.getStartDate().atTime(entry.getStartTime()),
                        entry.getStartDate().atTime(entry.getEndTime()), 0);
                entry.setUserObject(less);
            }
            entry.changeEndTime(endTimeField.getValue(), true);
            entry.getUserObject().setEnd(entry.getEndDate().atTime(endTimeField.getValue()));
        });

        // full day
        fullDay.setOnAction(evt -> entry.setFullDay(fullDay.isSelected()));

        EventHandler<ActionEvent> comboOnAction = evt -> {
            if (entry.getUserObject() == null) {
                Lesson less = new Lesson(studentBox.getValue(), languageBox.getValue(), entry.getStartDate(),
                        entry.getStartDate().atTime(entry.getStartTime()),
                        entry.getStartDate().atTime(entry.getEndTime()), 0);
                entry.setUserObject(less);
            }
            entry.getUserObject().setAtendingStudent(studentBox.getValue());
            entry.getUserObject().setLangClassType(languageBox.getValue());
            entry.setTitle(studentLabel.getText() + " "
                    + ((studentBox.getValue() != null) ? studentBox.getValue().getFullName() : "") + " \n"
                    + languageLabel.getText() + " "
                    + ((languageBox.getValue() != null) ? languageBox.getValue().getName() : ""));
        };
        // Student
        studentBox.setOnAction(comboOnAction);

        // Language
        languageBox.setOnAction(comboOnAction);

        entry.recurrenceRuleProperty().addListener(it ->

        updateRecurrenceRuleButton(entry));

        updateRecurrenceRuleButton(entry);

        entry.recurrenceRuleProperty().addListener(it -> updateSummaryLabel(entry));
    }

    private void updateSummaryLabel(Entry<?> entry) {
        String rule = entry.getRecurrenceRule();
        String text = Util.convertRFC2445ToText(rule, entry.getStartDate());
        summaryLabel.setText(text);
    }

    private void showRecurrenceEditor(Entry<?> entry) {
        RecurrencePopup popup = new RecurrencePopup();
        RecurrenceView recurrenceView = popup.getRecurrenceView();
        String recurrenceRule = entry.getRecurrenceRule();
        if (recurrenceRule == null || recurrenceRule.trim().equals("")) { //$NON-NLS-1$
            recurrenceRule = "RRULE:FREQ=DAILY;"; //$NON-NLS-1$
        }
        recurrenceView.setRecurrenceRule(recurrenceRule);
        popup.setOnOkPressed(evt -> {
            String rrule = recurrenceView.getRecurrenceRule();
            entry.setRecurrenceRule(rrule);
        });

        Point2D anchor = recurrenceButton.localToScreen(0, recurrenceButton.getHeight());
        popup.show(recurrenceButton, anchor.getX(), anchor.getY());
    }

    private void updateRecurrenceRule(Entry<?> entry, String rule) {
        entry.setRecurrenceRule(rule);
    }

    private void updateRecurrenceRuleButton(Entry<?> entry) {
        String rule = entry.getRecurrenceRule();
        if (rule == null) {
            recurrenceButton.setText(Messages.getString("EntryDetailsView.NONE")); //$NON-NLS-1$
        } else {
            switch (rule.trim().toUpperCase()) {
            case "RRULE:FREQ=DAILY": //$NON-NLS-1$
                recurrenceButton.setText(Messages.getString("EntryDetailsView.DAILY")); //$NON-NLS-1$
                break;
            case "RRULE:FREQ=WEEKLY": //$NON-NLS-1$
                recurrenceButton.setText(Messages.getString("EntryDetailsView.WEEKLY")); //$NON-NLS-1$
                break;
            case "RRULE:FREQ=MONTHLY": //$NON-NLS-1$
                recurrenceButton.setText(Messages.getString("EntryDetailsView.MONTHLY")); //$NON-NLS-1$
                break;
            case "RRULE:FREQ=YEARLY": //$NON-NLS-1$
                recurrenceButton.setText(Messages.getString("EntryDetailsView.YEARLY")); //$NON-NLS-1$
                break;
            default:
                recurrenceButton.setText(Messages.getString("EntryDetailsView.CUSTOM")); //$NON-NLS-1$
                break;
            }
        }
    }
}
