// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

/**
 * Everything that can be the content of a tile.
 **/
public interface TileContent {

    /**
     * Name of the Sprite of this object in the texture atlas.
     */
    String getSpriteName();

    /**
     * Getter for strength.
     *
     * @return The strength of this object which determines the capability of the
     * object to protect or attack in combat.
     */
    int getStrength();

    /**
     * Returns a copy of this object.
     */
    TileContent getCopy();

}
