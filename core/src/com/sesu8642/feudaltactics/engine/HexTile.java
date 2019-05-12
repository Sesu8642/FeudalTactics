package com.sesu8642.feudaltactics.engine;

import com.sesu8642.feudaltactics.gamelogic.Kingdom;

public class HexTile {
	
	private Player player;
	private MapObject content;
	private Kingdom kingdom;
	
	public HexTile(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public MapObject getContent() {
		return content;
	}	

	public void setContent(MapObject content) {
		this.content = content;
	}
	

	public Kingdom getKingdom() {
		return kingdom;
	}

	public void setKingdom(Kingdom kingdom) {
		this.kingdom = kingdom;
	}
	
	@Override
	public String toString() {
		String kingdomStr;
		kingdomStr = kingdom == null ? "null": kingdom.toString();
		return "Color: " + player.getColor().toString() + ", Kingdom: " + kingdomStr;
	}
}
