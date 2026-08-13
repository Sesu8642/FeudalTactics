// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.shared.events;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: Game state was pasted.
 */
public class GameStatePastedEvent {

    @Getter
    @Setter
    private GameState gameState;

    /**
     * Constructor.
     *
     * @param gameState pasted game state
     */
    public GameStatePastedEvent(GameState gameState) {
        this.gameState = gameState;
    }

}
