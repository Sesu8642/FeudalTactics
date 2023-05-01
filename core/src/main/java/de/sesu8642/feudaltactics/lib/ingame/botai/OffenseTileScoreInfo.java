// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame.botai;

import de.sesu8642.feudaltactics.lib.gamestate.HexTile;

/**
 * Container for a tile, its offense score (how valuable it is for conquering)
 * and the unit strength required to conquer it.
 */
class OffenseTileScoreInfo {

	public final HexTile tile;
	public final int score;
	public final int requiredStrength;

	public OffenseTileScoreInfo(HexTile tile, int score, int requiredStrength) {
		this.tile = tile;
		this.score = score;
		this.requiredStrength = requiredStrength;
	}

}