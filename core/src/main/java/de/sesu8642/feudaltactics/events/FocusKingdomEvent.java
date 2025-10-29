// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Kingdom;
import lombok.Getter;

/**
 * Event: Kingdom needs to be focused with the camera.
 */
public class FocusKingdomEvent {

    @Getter
    private final GameState gameState;
    @Getter
    private final Kingdom kingdom;

    /**
     * Constructor.
     *
     * @param kingdom kingdom to be focused
     */
    public FocusKingdomEvent(GameState gameState, Kingdom kingdom) {
        this.gameState = gameState;
        this.kingdom = kingdom;
    }

}
