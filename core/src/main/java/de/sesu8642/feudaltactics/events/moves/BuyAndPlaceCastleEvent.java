// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events.moves;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: User buys a castle and places it immediately.
 */
public class BuyAndPlaceCastleEvent {

    @Getter
    @Setter
    private Vector2 worldCoords;

    public BuyAndPlaceCastleEvent(Vector2 worldCoords) {
        this.worldCoords = worldCoords;
    }

}
