// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

/**
 * Map object representing a gravestone. A gravestone spawns when a unit dies
 * because it cannot be paid. After one turn, a gravestone will turn into a
 * tree.
 **/
public class Gravestone implements MapObject, Blocking {

	public static final String SPRITE_NAME = "gravestone";
	private static final int STRENGTH = 0;

	@Override
	public String getSpriteName() {
		return SPRITE_NAME;
	}

	@Override
	public int getStrength() {
		return STRENGTH;
	}

	@Override
	public Gravestone getCopy() {
		return new Gravestone();
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
