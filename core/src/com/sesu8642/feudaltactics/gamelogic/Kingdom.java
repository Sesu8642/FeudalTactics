package com.sesu8642.feudaltactics.gamelogic;

import com.sesu8642.feudaltactics.engine.HexTile;
import com.sesu8642.feudaltactics.engine.Player;

public class Kingdom {
	
	private HexTile[] tiles;
	private Capital capital;
	private Player player;
	
	public HexTile[] getTiles() {
		return tiles;
	}
	public Capital getCapital() {
		return capital;
	}
	public Player getPlayer() {
		return player;
	}
	
}
