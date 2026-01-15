// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import lombok.Getter;

/**
 * Event: Game is exited to menu or new game.
 */
public class GameExitedEvent {

    @Getter
    private GameState gameState;

    @Getter
    private NewGamePreferences gamePreferences;

    /**
     * Constructor.
     *
     * @param gameState current game state
     */
    public GameExitedEvent(GameState gameState, NewGamePreferences gamePreferences) {
        this.gameState = gameState;
        this.gamePreferences = gamePreferences;
    }
}
