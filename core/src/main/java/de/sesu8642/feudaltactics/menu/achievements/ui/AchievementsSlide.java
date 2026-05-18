// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.sesu8642.feudaltactics.menu.achievements.AchievementService;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the slide in the achievements screen.
 */
@Singleton
public class AchievementsSlide extends Slide {

    private final HorizontalGroup achievementTileGroup;

    @Getter
    private final List<@NonNull AchievementBox> achievementBoxes;

    @Inject
    public AchievementsSlide(Skin skin, AchievementService achievementService, DialogFactory dialogFactory) {
        super(skin, "Achievements");

        achievementTileGroup = new HorizontalGroup();
        achievementTileGroup.wrap();
        achievementTileGroup.space(10);
        achievementTileGroup.wrapSpace(10);
        achievementTileGroup.align(Align.center);

        List<@NonNull AbstractAchievement> achievements = achievementService.getAchievements();
        achievementBoxes = new java.util.ArrayList<>();
        for (AbstractAchievement achievement : achievements) {
            achievementBoxes.add(new AchievementBox(skin, achievement, dialogFactory));
        }

        renderAchievements();

        getTable().add(achievementTileGroup).fill().expand();
    }

    /**
     * Refresh the achievement tiles from the current service state.
     * Call this when the screen becomes visible to update the UI.
     */
    public void renderAchievements() {
        achievementTileGroup.clearChildren();
        for (AchievementBox achievementBox : achievementBoxes) {
            Actor achievementActor = achievementBox.displayAchievement();
            achievementTileGroup.addActor(achievementActor);
        }
    }
}
