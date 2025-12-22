// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;
import de.sesu8642.feudaltactics.platformspecific.Insets;

/**
 * Represents the stage for the statistics screen.
 */
@Singleton
public class StatisticsStage extends SlideStage {

    @Inject
    public StatisticsStage(StatisticsDao statisticsDao, StatisticsSlide statisticsSlide,
                            @MenuViewport Viewport viewport, Insets insets, @MenuCamera OrthographicCamera camera,
                            Skin skin, ScreenNavigationController screenNavigationController) {
        super(viewport, Collections.singletonList(statisticsSlide), insets,
            screenNavigationController::transitionToMainMenuScreen, camera, skin);
    }
}