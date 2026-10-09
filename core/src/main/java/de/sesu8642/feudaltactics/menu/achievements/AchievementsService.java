// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements;

import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;
import de.sesu8642.feudaltactics.shared.events.RegenerateMapEvent;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Service for managing achievements, used to track and update player progress.
 */
@Singleton
public class AchievementsService {
    private final AchievementsRepository achievementRepository;
    @Getter
    private final List<AbstractAchievement> achievements;

    @Inject
    public AchievementsService(
        AchievementsRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
        achievements = achievementRepository.loadPersistedAchievements();
    }

    /**
     * Called when a game is exited. Called from AchievementsEventHandler and forwards the event to all achievements.
     */
    public void onGameExited(GameExitedEvent event) {
        for (AbstractAchievement achievement : achievements) {
            if (achievement.onGameExited(event)) {
                storeAchievementProgress(achievement);
            }
        }
    }

    /**
     * Called when the map is regenerated. Called from AchievementsEventHandler and forwards the event to all
     * achievements.
     */
    public void onMapRegeneration(RegenerateMapEvent event) {
        for (AbstractAchievement achievement : achievements) {
            if (achievement.onMapRegeneration(event)) {
                storeAchievementProgress(achievement);
            }
        }
    }

    private void storeAchievementProgress(AbstractAchievement achievement) {
        achievementRepository.storeProgress(achievement.getId(), achievement.getProgress());
        if (achievement.isUnlocked()) {
            achievementRepository.unlockAchievement(achievement.getId());
        } else {
            if (achievement instanceof AchievementNeedsFullStorage) {
                achievementRepository.storeFullAchievementData(achievement.getId(),
                    ((AchievementNeedsFullStorage) achievement).serializeToJson());
            }
        }
    }
}
