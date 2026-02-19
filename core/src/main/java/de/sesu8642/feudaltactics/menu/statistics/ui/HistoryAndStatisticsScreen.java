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
 * Represents the UI screen for displaying history and statistics.
 */
@Singleton
public class HistoryAndStatisticsScreen extends GameScreen {

    /**
     * Constructor.
     */
    @Inject
    public HistoryAndStatisticsScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                                      HistoryAndStatisticsStage stage) {
        super(camera, viewport, stage);
    }

    @Override
    public void show() {
        super.show();

        final HistoryAndStatisticsStage statisticsStage = (HistoryAndStatisticsStage) getActiveStage();
        // Refresh all slides when screen is shown
        statisticsStage.getStatisticsSlide().refreshStatistics();
        statisticsStage.getHistorySlide().refreshHistory();
    }
}
