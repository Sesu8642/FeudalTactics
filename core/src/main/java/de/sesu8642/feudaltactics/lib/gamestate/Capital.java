// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

/**
 * Map object representing the capital of a kingdom. If the capital is
 * destroyed, the kingdom's money is lost.
 **/
public class Capital implements MapObject {

    public static final String SPRITE_NAME = "capital";
    public static final int STRENGTH = 1;

    @Override
    public String getSpriteName() {
        return SPRITE_NAME;
    }

    @Override
    public int getStrength() {
        return STRENGTH;
    }

    @Override
    public Capital getCopy() {
        return new Capital();
    }

    @Override
    public String toString() {
        return getClass().getName();
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
