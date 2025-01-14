// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Kingdom;

/**
 * Event: Kingdom needs to be focused with the camera.
 */
public class FocusKingdomEvent {

    private final GameState gameState;
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

    public GameState getGameState() {
        return gameState;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

}
