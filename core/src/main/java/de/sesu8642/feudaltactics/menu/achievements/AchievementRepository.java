// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements;

import com.badlogic.gdx.Preferences;

import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.dagger.AchievementsPrefStore;
import de.sesu8642.feudaltactics.menu.achievements.model.AbortGameAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.HistoricPersonOrEvent;
import de.sesu8642.feudaltactics.menu.achievements.model.LoseAgainstWeakestAiAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinAgainstAiLevelAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinAgainstManyEnemiesAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.PlayMoreThanNRoundsAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinInNRoundsAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinNGamesAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinOnMapSizeAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinVeryHardGamesInARowAchievement;
import de.sesu8642.feudaltactics.menu.achievements.model.WinWhenStartingLastAchievement;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
}
