// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import lombok.ToString;

/**
 * Map object representing a castle. Castles are defensive structures.
 **/
@ToString
public class Castle implements TileContent {

    public static final int COST = 15;
    public static final int STRENGTH = 2;

    @Override
    public int getStrength() {
        return STRENGTH;
    }

    @Override
    public Castle getCopy() {
        return new Castle();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        return getClass() == obj.getClass();
    }

}
