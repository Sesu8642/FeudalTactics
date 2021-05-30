package com.sesu8642.feudaltactics.gamestate;

import java.util.LinkedHashSet;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Tree;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;

public class Kingdom {

	private LinkedHashSet<HexTile> tiles = new LinkedHashSet<HexTile>();
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

	public int getIncome() {
		int income = tiles.size();
		for (HexTile tile : tiles) {
			if (tile.getContent() != null
					&& ClassReflection.isAssignableFrom(tile.getContent().getClass(), Tree.class)) {
				income -= 1;
			}
		}
		return income;
	}

	public int getSalaries() {
		int salaries = 0;
		for (HexTile tile : tiles) {
			if (tile.getContent() != null
					&& ClassReflection.isAssignableFrom(tile.getContent().getClass(), Unit.class)) {
				salaries += ((Unit) tile.getContent()).getUnitType().salary();
			}
		}
		return salaries;
	}

	public LinkedHashSet<HexTile> getTiles() {
		return tiles;
	}

	public void setTiles(LinkedHashSet<HexTile> tiles) {
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
		return super.toString() + "; savings: " + getSavings() + ", income: " + getIncome() + ", salaries: "
				+ getSalaries();
	}

}
