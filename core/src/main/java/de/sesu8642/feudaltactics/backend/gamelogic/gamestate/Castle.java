// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.gamelogic.gamestate;

/** Map object representing a castle. Castles are defensive structures. **/
public class Castle implements MapObject {

	public static final int COST = 15;
	public static final String SPRITE_NAME = "castle";
	public static final int STRENGTH = 2;

	@Override
	public String getSpriteName() {
		return SPRITE_NAME;
	}

	@Override
	public int getStrength() {
		return STRENGTH;
	}

	@Override
	public Castle getCopy() {
		return new Castle();
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		return true;
	}

}
