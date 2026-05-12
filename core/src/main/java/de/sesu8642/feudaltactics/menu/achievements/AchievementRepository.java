// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements;

import com.badlogic.gdx.Preferences;

import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.menu.achievements.dagger.AchievementsPrefStore;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Repository for storing achievements, used by AchievementService.
 */
@Singleton
public class AchievementRepository {

    /**
     * Name of the preferences store for achievements.
     */
    public static final String ACHIEVEMENTS_NAME = "achievements";

    private final Preferences prefStore;

    @Inject
    public AchievementRepository(
            @AchievementsPrefStore Preferences achievementsPrefs,
            AutoSaveRepository autoSaveRepository) {
        this.prefStore = achievementsPrefs;
    }

    public void LoadPersistedAchievements(Iterable<AbstractAchievement> achievements) {
        for (AbstractAchievement achievement : achievements) {
            achievement.setUnlocked(
                prefStore.getBoolean("achievement-" + achievement.getId(), false));
            achievement.setProgress(
                prefStore.getInteger("achievement-progress-" + achievement.getId(), 0));
            if (achievement instanceof AchievementNeedsFullStorage) {
                String serializedData = prefStore.getString("achievement-full-" + achievement.getId(), null);
                if (serializedData != null) {
                    ((AchievementNeedsFullStorage) achievement).deserializeFromJson(serializedData);
                }
            }
        }
    }

    /**
     * Unlocks the achievement with the given ID.
     */
    public void unlockAchievement(String achievementId) {
        prefStore.putBoolean("achievement-" + achievementId, true);
        prefStore.flush();
    }

    /**
     * Stores the progress for the achievement with the given ID.
     */
    public void storeProgress(String id, int number) {
        prefStore.putInteger("achievement-progress-" + id, number);
        prefStore.flush();
    }

    /**
     * Stores the full achievement data for achievements that implement AchievementNeedsFullStorage.
     * @param id The ID of the achievement.
     * @param serializedData The serialized data (JSON) of the achievement.
     */
    public void storeFullAchievementData(String id, String serializedData) {
        prefStore.putString("achievement-full-" + id, serializedData);
        prefStore.flush();
    }
}
