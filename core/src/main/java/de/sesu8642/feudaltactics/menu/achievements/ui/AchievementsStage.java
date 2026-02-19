// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

/**
 * Represents the stage for the achievements screen.
 */
@Singleton
public class AchievementsStage extends SlideStage {

    @Getter
    private final AchievementsSlide achievementsSlide;

    @Inject
    public AchievementsStage(AchievementsSlide achievementsSlide,
                             @MenuViewport Viewport viewport, PlatformInsetsProvider platformInsetsProvider,
                             @MenuCamera OrthographicCamera camera,
                             Skin skin, ScreenNavigationController screenNavigationController) {
        super(viewport, Collections.singletonList(achievementsSlide), platformInsetsProvider,
            screenNavigationController::transitionToMainMenuScreen, camera, skin);
        this.achievementsSlide = achievementsSlide;
    }

    @Override
    public void updateOnResize(int width, int height) {
        super.updateOnResize(width, height);
        achievementsSlide.refreshAchievements();
    }
}
