// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

/**
 * Map object representing a tree. A tree prevent the tile it stands on from
 * generating income. Trees have a chance to spread to neighboring tiles.
 **/
public class Tree implements MapObject, Blocking {

	public static final String SPRITE_NAME = "tree";
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
	public Tree getCopy() {
		return new Tree();
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
