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

import javafx.beans.binding.Bindings;
import javafx.scene.Node;

public class LessonWeekPageSkin extends LessonPageBaseSkin<LessonWeekPage> {

    public LessonWeekPageSkin(LessonWeekPage view) {
        super(view);
    }

    @Override
    protected Node createContent() {
        LessonWeekPage weekPage = getSkinnable();
        LessonDetailedWeekView detailedWeekView = weekPage.getDetailedWeekView();

        weekPage.bind(detailedWeekView, true);

        Bindings.bindBidirectional(detailedWeekView.startTimeProperty(), weekPage.startTimeProperty());
        Bindings.bindBidirectional(detailedWeekView.endTimeProperty(), weekPage.endTimeProperty());

        return detailedWeekView;
    }
}
