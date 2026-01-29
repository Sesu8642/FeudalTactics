// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.GameExitedEvent;

import javax.inject.Inject;

/**
 * Handles events for the achievements screen.
 */
public class AchievementsEventHandler {

    @Inject
    public AchievementsEventHandler() {
    }

    @Subscribe
    public void handleGameExited(GameExitedEvent event) {
        // TODO: Handle achievements based on game exit events
    }
}
