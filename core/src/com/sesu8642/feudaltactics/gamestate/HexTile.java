package com.sesu8642.feudaltactics.gamestate;

import com.sesu8642.feudaltactics.gamestate.mapobjects.Kingdom;

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
		String kingdomStr = kingdom == null ? "null": kingdom.toString();
		String contentStr = content == null ? "null": content.toString();
		return "Color: " + player.getColor().toString() + ", Kingdom: " + kingdomStr + ", Content: " + contentStr;
	}
}
