// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame.botai;

import de.sesu8642.feudaltactics.lib.gamestate.HexTile;

/**
 * Container for a tile and a score. The meaning of the score depends on the
 * usage.
 */
class TileScoreInfo {

	public final HexTile tile;
	public final int score;

	public TileScoreInfo(HexTile tile, int score) {
		this.tile = tile;
		this.score = score;
	}

}
