// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: User tapped or clicked.
 */
public class TapInputEvent {

    @Getter
    @Setter
    private Vector2 worldCoords;
    @Getter
    @Setter
    private int count;

    public TapInputEvent(Vector2 worldCoords, int count) {
        this.worldCoords = worldCoords;
        this.count = count;
    }

}
