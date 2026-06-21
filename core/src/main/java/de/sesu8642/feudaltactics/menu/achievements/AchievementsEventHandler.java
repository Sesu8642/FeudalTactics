// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;
import de.sesu8642.feudaltactics.shared.events.RegenerateMapEvent;

import javax.inject.Inject;

/**
 * Handles events for the achievements screen.
 */
public class AchievementsEventHandler {

    private final AchievementsService achievementService;

    @Inject
    public AchievementsEventHandler(AchievementsService achievementService) {
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
}
