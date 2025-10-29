// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.ingame.GameParameters;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: Map needs to be re-generated because the Parameters of the generated
 * map changed or the player wants to retry or starts a new game.
 */
public class RegenerateMapEvent {

    @Getter
    @Setter
    private GameParameters gameParams;

    /**
     * Constructor.
     */
    public RegenerateMapEvent(GameParameters gameParams) {
        super();
        this.gameParams = gameParams;
    }

}
