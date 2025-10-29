// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.ingame.botai.Speed;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: Speed of bot turns is changed.
 */
public class BotTurnSpeedChangedEvent {

    @Getter
    @Setter
    private Speed speed;

    /**
     * Constructor.
     *
     * @param speed new speed.
     */
    public BotTurnSpeedChangedEvent(Speed speed) {
        this.speed = speed;
    }

}
