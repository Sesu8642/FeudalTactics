package de.sesu8642.feudaltactics.menu.achievements;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.menu.achievements.dagger.AchievementsPrefStore;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Data access object for achievements
 */
@Singleton
public class AchievementsDao {
// SPDX-License-Identifier: GPL-3.0-or-later

    public static final String ACHIEVEMENTS_NAME = "achievements";

    // This is the seed that has been played the most times in a row

    private final Preferences prefStore;

    private final AchievementRegistry achievementRegistry;

    @Inject
    public AchievementsDao(@AchievementsPrefStore Preferences achievementsPrefs, AchievementRegistry achievementRegistry) {
        prefStore = achievementsPrefs;
        this.achievementRegistry = achievementRegistry;
    }

    public void unlockAchievement(String achievementId) {
        prefStore.putBoolean("achievement-" + achievementId, true);
        prefStore.flush();
    }

    /**
     * Loads all achievements and sets their unlocked status.
     *
     * @return the loaded achievements
     */
    public List<AbstractAchievement> getAchievements() {
        return achievementRegistry.getAllAchievements().stream()
            .peek(achievement -> achievement.setUnlocked(
                prefStore.getBoolean("achievement-" + achievement.getId(), false)))
            .toList();
    }
}

