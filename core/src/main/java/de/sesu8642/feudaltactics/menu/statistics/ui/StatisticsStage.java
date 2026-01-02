// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.menu.common.ui.TabbedSlideStage;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

/**
 * Represents the stage for the statistics screen with tabbed navigation.
 * Contains tabs for Statistics, History, Achievements, and a Back button.
 */
@Singleton
public class StatisticsStage extends TabbedSlideStage {

    private static final List<String> TAB_NAMES = Arrays.asList("Stats", "History", "Achievements", "Back");

    @Getter
    private final StatisticsSlide statisticsSlide;
    @Getter
    private final HistorySlide historySlide;
    @Getter
    private final AchievementsSlide achievementsSlide;

    @Inject
    public StatisticsStage(StatisticsSlide statisticsSlide,
                           HistorySlide historySlide,
                           AchievementsSlide achievementsSlide,
                           @MenuViewport Viewport viewport,
                           PlatformInsetsProvider platformInsetsProvider,
                           @MenuCamera OrthographicCamera camera,
                           Skin skin,
                           ScreenNavigationController screenNavigationController) {
        super(viewport,
              Arrays.asList(statisticsSlide, historySlide, achievementsSlide),
              TAB_NAMES,
              platformInsetsProvider,
              screenNavigationController::transitionToMainMenuScreen,
              camera,
              skin);
        this.statisticsSlide = statisticsSlide;
        this.historySlide = historySlide;
        this.achievementsSlide = achievementsSlide;
    }
}
