package com.sesu8642.feudaltactics.gamelogic;

import java.util.ArrayList;

import com.sesu8642.feudaltactics.engine.HexTile;
import com.sesu8642.feudaltactics.engine.Player;

public class Kingdom {
	
	private ArrayList<HexTile> tiles;
	private Capital capital;
	private Player player;
	
	public Kingdom(Player player) {
		this.player = player;
		this.tiles = new ArrayList<HexTile>();
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
	
}
