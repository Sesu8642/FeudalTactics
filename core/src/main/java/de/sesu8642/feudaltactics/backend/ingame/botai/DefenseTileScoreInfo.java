// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.ingame.botai;

import de.sesu8642.feudaltactics.backend.gamestate.HexTile;

/**
 * Container for a tile and its defense score (how valuable it is for
 * protecting).
 */
class DefenseTileScoreInfo {

	public final HexTile tile;
	public final int score;

	public DefenseTileScoreInfo(HexTile tile, int score) {
		this.tile = tile;
		this.score = score;
	}

}
