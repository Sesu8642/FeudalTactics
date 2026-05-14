// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import lombok.Getter;

/**
 * Service for managing achievements, used to track and update player progress.
 */
@Singleton
public class AchievementService {
    private final AchievementRepository achievementRepository;

    @Getter
    private final List<AbstractAchievement> achievements;

    @Inject
    public AchievementService(
        AchievementRepository achievementRepository,
        AchievementProvider achievementProvider) {
        this.achievementRepository = achievementRepository;
        this.achievements = achievementProvider.CreateAchievements();

        achievementRepository.LoadPersistedAchievements(achievements);
    }

    /**
     * Called when a game is exited. Called from AchievementsEventHandler and forwards the event to all achievements.
     */
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        for (AbstractAchievement achievement : achievements) {
            if (achievement.onGameExited(event)) {
                    storeAchievementProgress(achievement);
            }
        }
    }

    /**
     * Called when the map is regenerated. Called from AchievementsEventHandler and forwards the event to all achievements.
     */
    public void onMapRegeneration(RegenerateMapEvent event) {
        for (AbstractAchievement achievement : achievements) {
            if (achievement.onMapRegeneration(event)) {
                storeAchievementProgress(achievement);
            }
        }
    }

    public void storeAchievementProgress(AbstractAchievement achievement) {
        if (achievement.isUnlocked()) {
            achievementRepository.unlockAchievement(achievement.getId());
        } else {
            achievementRepository.storeProgress(achievement.getId(), achievement.getProgress());
            if (achievement instanceof AchievementNeedsFullStorage) {
                achievementRepository.storeFullAchievementData(achievement.getId(), ((AchievementNeedsFullStorage) achievement).serializeToJson());
            }
        }
    }
}
