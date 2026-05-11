// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.achievements.AchievementProgressEvent;
import de.sesu8642.feudaltactics.events.achievements.AchievementUnlockedEvent;

import javax.inject.Inject;

/**
 * Handles events for the achievements screen.
 */
public class AchievementsEventHandler {

    private final AchievementService achievementService;

    @Inject
    public AchievementsEventHandler(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    /**
     * Handles the GameExitedEvent by forwarding it to the achievement service, which will then forward it to all achievements.
     */
    @Subscribe
    public void handleGameExited(GameExitedEvent event) {
        achievementService.onGameExited(event);
    }

    /**
    * Handles the RegenerateMapEvent by forwarding it to the achievement service, which will then forward it to all achievements.
    */
    @Subscribe
    public void handleMapRegeneration(RegenerateMapEvent event) {
        achievementService.onMapRegeneration(event);
    }

    @Subscribe
    public void handleAchievementUnlocked(AchievementUnlockedEvent event) {
        achievementService.onAchievementUnlocked(event);
    }

    @Subscribe
    public void handleAchievementProgress(AchievementProgressEvent event) {
        achievementService.onAchievementProgress(event);
    }
}
