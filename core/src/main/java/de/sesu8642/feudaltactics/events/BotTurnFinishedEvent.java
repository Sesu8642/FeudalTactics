// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: Bot player finished its turn.
 */
public class BotTurnFinishedEvent {

    @Getter
    @Setter
    private GameState gameState;

    /**
     * Constructor.
     *
     * @param gameState new game state
     */
    public BotTurnFinishedEvent(GameState gameState) {
        this.gameState = gameState;
    }
}
