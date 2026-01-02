// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the UI screen for displaying statistics.
 */
@Singleton
public class StatisticsScreen extends GameScreen {
    @Inject
    public StatisticsScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                            StatisticsStage stage) {
        super(camera, viewport, stage);
    }

    @Override
    public void show() {
        super.show();

        final StatisticsStage statisticsStage = (StatisticsStage) getActiveStage();
        statisticsStage.getStatisticsSlide().refreshStatistics();
    }
}
