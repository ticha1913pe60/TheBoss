package com.petarzlatev.languageclasses.view;
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

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.view.DayViewBase;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.util.Callback;

/**
 * A view representing a single day. This view can be customized to show all 24
 * hours at equal height, compress the early and late hours (unused hours), or
 * to hide these hours altogether.
 * <p/>
 * The view uses a factory to create instances of {@link DayEntryView}.
 * Applications can plug in their own factory to customize the appearance of
 * entry views.
 * <p/>
 * New calendar entries can be created by double clicking in the background of
 * the view.
 * <p/>
 * The image below shows an example of this view with three entry views and
 * compressed early hours.
 * <p/>
 * <center><img src="doc-files/day-view.png"></center>
 * <p/>
 */
public class LessonDayView extends DayViewBase {

	private static final String DAY_VIEW = "day-view"; //$NON-NLS-1$
	private static final String DAY_VIEW_TODAY = "today"; //$NON-NLS-1$
	private static final String DAY_VIEW_WEEKEND_DAY = "weekend-day"; //$NON-NLS-1$

	/**
	 * Constructs a new day view.
	 */
	public LessonDayView() {
		getStyleClass().add(DAY_VIEW);

		todayProperty().addListener(evt -> updateStyleClasses());
		dateProperty().addListener(evt -> updateStyleClasses());
		selectionModeProperty().addListener(evt -> getSelections().clear());
		getWeekendDays().addListener((Observable it) -> updateStyleClasses());
		updateStyleClasses();

		setEntryViewFactory(LessonDayEntryView::new);

		new LessonCreateDeleteHandler(this);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new LessonDayViewSkin<>(this);
	}

	private void updateStyleClasses() {
		LocalDate date = getDate();
		if (date.equals(getToday())) {
			if (!getStyleClass().contains(DAY_VIEW_TODAY)) {
				getStyleClass().add(DAY_VIEW_TODAY);
			}
		} else {
			getStyleClass().remove(DAY_VIEW_TODAY);
		}

		if (getWeekendDays().contains(date.getDayOfWeek())) {
			if (!getStyleClass().contains(DAY_VIEW_WEEKEND_DAY)) {
				getStyleClass().add(DAY_VIEW_WEEKEND_DAY);
			}
		} else {
			getStyleClass().remove(DAY_VIEW_WEEKEND_DAY);
		}
	}

	@Override
	public Optional<Calendar> getCalendarAt(double x, double y) {
		if (getLayout().equals(Layout.SWIMLANE)) {
			List<Calendar> visibleCalendars = getCalendars()
					.filtered(this::isCalendarVisible);
			double calendarWidth = getWidth() / visibleCalendars.size();
			int index = (int) (x / calendarWidth);
			if (index < visibleCalendars.size()) {
				return Optional.of(visibleCalendars.get(index));
			}
		}

		return Optional.empty();
	}

	private final ObjectProperty<Callback<Entry<?>, LessonDayEntryView>> entryViewFactory = new SimpleObjectProperty<>(
			this, "entryViewFactory"); //$NON-NLS-1$

	/**
	 * A factory used for creating instances of {@link DayEntryView} for each
	 * calendar entry that needs to be shown in this day view.
	 *
	 * @return the day entry view factory
	 */
	public final ObjectProperty<Callback<Entry<?>, LessonDayEntryView>> entryViewFactoryProperty() {
		return entryViewFactory;
	}

	/**
	 * Returns the value of {@link #entryViewFactoryProperty()}.
	 *
	 * @return the entry view factory
	 */
	public final Callback<Entry<?>, LessonDayEntryView> getEntryViewFactory() {
		return entryViewFactoryProperty().get();
	}

	/**
	 * Sets the value of {@link #entryViewFactoryProperty()}.
	 *
	 * @param factory
	 *            the entry view factory
	 */
	public final void setEntryViewFactory(
			Callback<Entry<?>, LessonDayEntryView> factory) {
		requireNonNull(factory);
		entryViewFactoryProperty().set(factory);
	}
}
