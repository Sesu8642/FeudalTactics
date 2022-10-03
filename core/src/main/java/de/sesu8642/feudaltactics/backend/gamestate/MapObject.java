// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.gamestate;

/** Everything that can be the content of a tile is a map object. **/
public interface MapObject {

	String getSpriteName();

	/**
	 * Getter for strength.
	 * 
	 * @return The strength of this object which determines the capability of the
	 *         object to protect or attack in combat.
	 */
	int getStrength();

	MapObject getCopy();

}
