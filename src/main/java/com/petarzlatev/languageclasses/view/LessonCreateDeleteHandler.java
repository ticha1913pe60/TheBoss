package com.petarzlatev.languageclasses.view;

import static java.util.Objects.requireNonNull;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Optional;

/*
 *  Copyright (C) 2017 Dirk Lemmermann Software & Consulting (dlsc.com)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.util.LoggingDomain;
import com.calendarfx.view.DateControl;
import com.calendarfx.view.ZonedDateTimeProvider;
import com.petarzlatev.languageclasses.Messages;
import com.petarzlatev.languageclasses.SystemMessages;
import com.petarzlatev.languageclasses.dao.LessonDAO;
import com.petarzlatev.languageclasses.model.DataSource;
import com.petarzlatev.languageclasses.model.Lesson;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class LessonCreateDeleteHandler {
	private final LessonDAO database = DataSource.getInstance().getLessonDAO();

    private DateControl dateControl;

    public LessonCreateDeleteHandler(DateControl control) {
        this.dateControl = requireNonNull(control);

        dateControl.addEventHandler(MouseEvent.MOUSE_CLICKED, this::createEntry);
        dateControl.addEventHandler(KeyEvent.KEY_PRESSED, this::deleteEntries);
    }

    private void createEntry(MouseEvent evt) {
        if (evt.getButton().equals(MouseButton.PRIMARY) && evt.getClickCount() == 2) {
            LoggingDomain.VIEW.fine("create entry mouse event received inside control: " + dateControl.getClass().getSimpleName());

            ZonedDateTime time = ZonedDateTime.now();
            if (dateControl instanceof ZonedDateTimeProvider) {
                ZonedDateTimeProvider provider = (ZonedDateTimeProvider) dateControl;
                time = provider.getZonedDateTimeAt(evt.getX(), evt.getY());
            }

            Optional<Calendar> calendar = dateControl.getCalendarAt(evt.getX(), evt.getY());

            if (time != null) {
                dateControl.createEntryAt(time, calendar.orElse(null));
            }
        }
    }

    private void deleteEntries(KeyEvent evt) {
        switch (evt.getCode()) {
            case DELETE:
            case BACK_SPACE:
                for (Entry<?> entry : dateControl.getSelections()) {
                    if (!dateControl.getEntryEditPolicy().call(new DateControl.EntryEditParameter(dateControl, entry, DateControl.EditOperation.DELETE))) {
                        continue;
                    }
                    if (entry.isRecurrence()) {
                        entry = entry.getRecurrenceSourceEntry();
                    }
                    if (!dateControl.getEntryEditPolicy().call(new DateControl.EntryEditParameter(dateControl, entry, DateControl.EditOperation.DELETE))) {
                        continue;
                    }

                    Calendar calendar = entry.getCalendar();
                    if (calendar != null && !calendar.isReadOnly()) {
                        entry.removeFromCalendar();
                        try {
							if (entry.getUserObject() != null && ((Lesson) entry.getUserObject()).getLessonID() > 0) {
								database.deleteLesson(((Lesson) entry.getUserObject()).getLessonID());
							}
						} catch (SQLException e1) {
							SystemMessages.showErrorMsg(
									Messages.getString("Error.ERROR_SQL_QUERY") + " " + e1.getMessage(),
									Messages.getString("System.ERROR"));
						}
                    }
                }
                dateControl.clearSelection();
                break;
            case F5:
                dateControl.refreshData();
            default:
                break;
        }
    }
}