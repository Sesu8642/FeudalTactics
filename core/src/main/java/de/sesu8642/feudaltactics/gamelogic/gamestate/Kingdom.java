// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.gamelogic.gamestate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Group of connected tiles that belong to the same player. **/
public class Kingdom {

	// need a list to have consistent iteration order; LinkedHashSet doesn't work
	// because the tiles can change
	private List<HexTile> tiles = new ArrayList<>();
	private Player player;
	private int savings = 0;
	// only used by ai
	private boolean doneMoving = false;
	// for displaying a hint when the player forgets the kingdom
	private boolean wasActiveInCurrentTurn = false;

	public Kingdom() {
	}

	public Kingdom(Player player) {
		this.player = player;
	}

	public List<HexTile> getTiles() {
		return tiles;
	}

	public void setTiles(List<HexTile> tiles) {
		this.tiles = tiles;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getSavings() {
		return savings;
	}

	public void setSavings(int savings) {
		this.savings = savings;
	}

	public boolean isDoneMoving() {
		return doneMoving;
	}

	public void setDoneMoving(boolean doneMoving) {
		this.doneMoving = doneMoving;
	}

	public boolean isWasActiveInCurrentTurn() {
		return wasActiveInCurrentTurn;
	}

	public void setWasActiveInCurrentTurn(boolean wasActiveInCurrentTurn) {
		this.wasActiveInCurrentTurn = wasActiveInCurrentTurn;
	}

	@Override
	public String toString() {
		return super.toString() + "; savings: " + getSavings();
	}

	@Override
	public int hashCode() {
		return Objects.hash(doneMoving, player, savings, tiles, wasActiveInCurrentTurn);
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
		Kingdom other = (Kingdom) obj;
		// ignore order of the tiles; not a perfect method but good enough since there
		// shouldnt be duplicates
		return doneMoving == other.doneMoving && Objects.equals(player, other.player) && savings == other.savings
				&& tiles.size() == other.tiles.size() && tiles.containsAll(other.tiles)
				&& wasActiveInCurrentTurn == other.wasActiveInCurrentTurn;
	}

}
