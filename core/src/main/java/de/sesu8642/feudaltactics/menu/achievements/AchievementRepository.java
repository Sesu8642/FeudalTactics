// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.menu.achievements.dagger.AchievementsPrefStore;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Repository for managing achievements. Combines achievement storage and retrieval.
 */
@Singleton
public class AchievementRepository {

    public static final String ACHIEVEMENTS_NAME = "achievements";

    private final Preferences prefStore;

    @Getter
    private final List<AbstractAchievement> achievements;

    @Inject
    public AchievementRepository(@AchievementsPrefStore Preferences achievementsPrefs) {
        this.prefStore = achievementsPrefs;

        // Create all achievements once
        this.achievements = List.of(
            new de.sesu8642.feudaltactics.menu.achievements.model.WinNGamesAchievement(this, 1),
            new de.sesu8642.feudaltactics.menu.achievements.model.WinNGamesAchievement(this, 10),
            new de.sesu8642.feudaltactics.menu.achievements.model.WinNGamesAchievement(this, 50)
        );

        LoadPersistedAchievements();
    }

    private void LoadPersistedAchievements() {
        for (AbstractAchievement achievement : achievements) {
            achievement.setUnlocked(
                prefStore.getBoolean("achievement-" + achievement.getId(), false));
            achievement.setProgress(
                prefStore.getInteger("achievement-progress-" + achievement.getId(), 0));
        }
    }

    public void unlockAchievement(String achievementId) {
        prefStore.putBoolean("achievement-" + achievementId, true);
        prefStore.flush();
    }

    public void storeProgress(String id, int number) {
        prefStore.putInteger("achievement-progress-" + id, number);
        prefStore.flush();
    }
}