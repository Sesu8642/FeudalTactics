// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;

/**
 * Represents the UI screen for displaying statistics.
 */
@Singleton
public class StatisticsScreen extends GameScreen {
    @Inject
    public StatisticsScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport, StatisticsStage stage) {
        super(camera, viewport, stage);
    }

    @Override
    public void show() {
        super.show();

        StatisticsStage statisticsStage = (StatisticsStage) getActiveStage();
        statisticsStage.getStatisticsSlide().refreshStatistics();
    }
}