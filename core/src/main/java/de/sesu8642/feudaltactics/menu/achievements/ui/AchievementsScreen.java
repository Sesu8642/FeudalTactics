// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the UI screen for displaying achievements.
 */
@Singleton
public class AchievementsScreen extends GameScreen {
    @Inject
    public AchievementsScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                              AchievementsStage stage) {
        super(camera, viewport, stage);
    }

    @Override
    public void show() {
        super.show();

        final AchievementsStage achievementsStage = (AchievementsStage) getActiveStage();
        achievementsStage.getAchievementsSlide().refreshAchievements();
    }
}
