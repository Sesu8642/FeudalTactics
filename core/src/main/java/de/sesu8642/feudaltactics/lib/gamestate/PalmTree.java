// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

/**
 * Map object representing a palm tree. A palm tree prevents the tile it stands
 * on from generating income. Palm trees spawn on coast tiles and spread to
 * neighboring coast tiles on every turn.
 **/
public class PalmTree implements MapObject, Blocking {

	public static final String SPRITE_NAME = "palm_tree";
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
	public PalmTree getCopy() {
		return new PalmTree();
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
