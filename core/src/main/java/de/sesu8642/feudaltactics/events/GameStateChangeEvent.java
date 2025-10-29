// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: Game state changed.
 */
public class GameStateChangeEvent {

    @Getter
    @Setter
    private GameState gameState;

    /**
     * Constructor.
     *
     * @param gameState new game state
     */
    public GameStateChangeEvent(GameState gameState) {
        this.gameState = gameState;
    }

}
