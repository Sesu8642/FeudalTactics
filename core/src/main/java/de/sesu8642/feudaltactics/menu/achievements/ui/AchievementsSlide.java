// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the slide in the achievements screen.
 */
@Singleton
public class AchievementsSlide extends Slide {

    private final Skin skin;
    private final Table achievementsTable;
    private final AchievementRepository achievementRepository;

    /**
     * Constructor.
     *
     * @param skin game skin
     */
    @Inject
    public AchievementsSlide(Skin skin, AchievementRepository achievementRepository) {
        super(skin, "Achievements");
        this.skin = skin;
        this.achievementRepository = achievementRepository;

        achievementsTable = new Table();
        getTable().add(achievementsTable).fill().expand();
        refreshAchievements();
    }

    /**
     * Refreshes the achievements UI with the latest values.
     */
    public void refreshAchievements() {
        achievementsTable.clear();

        List<AbstractAchievement> achievements = achievementRepository.getAchievements();

        for (AbstractAchievement achievement : achievements) {
            Label nameLabel = new Label(achievement.getName(), skin);
            Label descLabel = new Label(achievement.getDescription(), skin);
            String progressText = achievement.isUnlocked()
                ? "Unlocked"
                : String.format("Progress: %d / %d", achievement.getProgress(), achievement.getGoal());
            Label progressLabel = new Label(progressText, skin);

            achievementsTable.add(nameLabel).left().row();
            achievementsTable.add(descLabel).left().row();
            achievementsTable.add(progressLabel).left().row();
            achievementsTable.add().height(10).row(); // Spacer
        }
    }
}
