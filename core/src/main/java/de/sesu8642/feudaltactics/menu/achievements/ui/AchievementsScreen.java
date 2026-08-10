// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingClickListener;
import de.sesu8642.feudaltactics.menu.common.ui.FeudalTacticsDialog;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the UI screen for displaying achievements.
 */
@Singleton
public class AchievementsScreen extends GameScreen {
    private final AchievementsStage achievementsStage;
    private final DialogFactory dialogFactory;
    private final LocalizationManager localizationManager;

    @Inject
    public AchievementsScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                              AchievementsStage achievementsStage, DialogFactory dialogFactory,
                              LocalizationManager localizationManager) {
        super(camera, viewport, achievementsStage);
        this.achievementsStage = achievementsStage;
        this.dialogFactory = dialogFactory;
        this.localizationManager = localizationManager;

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
        achievementsStage.getAchievementsSlide().getAchievementBoxes().forEach(achievementBox ->
            achievementBox.getAchievementWindow().addListener(new ExceptionLoggingClickListener(() -> {
                final AbstractAchievement achievement = achievementBox.getAchievement();
                final FeudalTacticsDialog achievementDetailsDialog = dialogFactory.createInformationDialog(() -> {
                });
                achievementDetailsDialog.headline(achievement.getTranslatedName(localizationManager));
                achievementDetailsDialog.text(achievement.getTranslatedDescription(localizationManager));
                achievementDetailsDialog.show(achievementsStage);
            })));
    }
}
