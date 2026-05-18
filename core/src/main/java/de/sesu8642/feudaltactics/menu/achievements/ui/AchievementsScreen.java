// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingClickListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;


import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the UI screen for displaying achievements.
 */
@Singleton
public class AchievementsScreen extends GameScreen {
    private final AchievementsStage achievementsStage;
    @Inject
    public AchievementsScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                              AchievementsStage stage) {
        super(camera, viewport, stage);
        this.achievementsStage = stage;

        registerEventListeners();
    }

    @Override
    public void show() {
        super.show();
        if (achievementsStage != null && achievementsStage.getAchievementsSlide() != null) {
            achievementsStage.getAchievementsSlide().renderAchievements();
        }
    }

    private void registerEventListeners() {
        achievementsStage.getAchievementsSlide().getAchievementBoxes().forEach(achievementBox -> {
            achievementBox.getAchievementWindow().addListener(new ExceptionLoggingClickListener(() -> {
                Dialog achievementDetailsDialog = achievementBox.createAchievementDetailsDialog();
                achievementDetailsDialog.show(achievementsStage);
            }));
        });
    }
}
