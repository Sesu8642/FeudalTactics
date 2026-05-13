// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.CyclingSlideStage;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.List;

/**
 * Represents the stage for the history and statistics screen with a button to switch between Statistics and History,
 * as well as a button to return to the main menu.
 */
@Singleton
public class HistoryAndStatisticsStage extends CyclingSlideStage {

    private static final List<String> BUTTON_TEXTS_KEYS = ImmutableList.of(TranslationKeys.MENU_BUTTON_GAME_HISTORY,
        TranslationKeys.MENU_BUTTON_STATISTICS);

    @Getter
    private final StatisticsSlide statisticsSlide;
    @Getter
    private final HistorySlide historySlide;

    @Inject
    public HistoryAndStatisticsStage(StatisticsSlide statisticsSlide,
                                     HistorySlide historySlide,
                                     @MenuViewport Viewport viewport,
                                     PlatformInsetsProvider platformInsetsProvider,
                                     @MenuCamera OrthographicCamera camera,
                                     Skin skin,
                                     ScreenNavigationController screenNavigationController,
                                     LocalizationManager localizationManager) {
        super(viewport,
            Arrays.asList(historySlide, statisticsSlide),
            BUTTON_TEXTS_KEYS,
            platformInsetsProvider,
            screenNavigationController::transitionToMainMenuScreen,
            camera,
            skin,
            localizationManager);
        this.statisticsSlide = statisticsSlide;
        this.historySlide = historySlide;
        // disable to make the horizontal group wrap
        disableHorizontalScrolling();
    }
}
