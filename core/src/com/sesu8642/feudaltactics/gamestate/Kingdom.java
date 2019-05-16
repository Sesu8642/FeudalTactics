package com.sesu8642.feudaltactics.gamestate;

import java.util.ArrayList;

import com.sesu8642.feudaltactics.gamestate.mapobjects.Capital;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Tree;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;

public class Kingdom {

	private ArrayList<HexTile> tiles;
	private Capital capital;
	private Player player;
	private int savings = 0;

	public Kingdom(Player player) {
		this.player = player;
		this.tiles = new ArrayList<HexTile>();
	}

	public int getIncome() {
		int income = tiles.size();
		for (HexTile tile : tiles) {
			if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Tree.class)) {
				income -= 1;
			}
		}
		return income;
	}

	public int getSalaries() {
		int salaries = 0;
		for (HexTile tile : tiles) {
			if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Unit.class)) {
				salaries += ((Unit) tile.getContent()).getUnitType().salary();
			}
		}
		return salaries;
	}

	public ArrayList<HexTile> getTiles() {
		return tiles;
	}

	public Capital getCapital() {
		return capital;
	}

	public Player getPlayer() {
		return player;
	}

	public int getSavings() {
		return savings;
	}

	public void setSavings(int savings) {
		this.savings = savings;
	}

}
