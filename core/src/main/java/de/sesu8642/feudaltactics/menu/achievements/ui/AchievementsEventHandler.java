// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

import javax.inject.Inject;

/**
 * Handles events for the achievements screen.
 */
public class AchievementsEventHandler {

    private final AchievementRepository achievementRepository;

    @Inject
    public AchievementsEventHandler(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    /**
     * Handles the GameExitedEvent by forwarding it to the achievement repository, which will then forward it to all achievements.
     */
    @Subscribe
    public void handleGameExited(GameExitedEvent event) {
        achievementRepository.onGameExited(event);
    }

    /**
    * Handles the RegenerateMapEvent by forwarding it to the achievement repository, which will then forward it to all achievements.
    */
    @Subscribe
    public void handleMapRegeneration(RegenerateMapEvent event) {
        achievementRepository.onMapRegeneration(event);
    }
}
