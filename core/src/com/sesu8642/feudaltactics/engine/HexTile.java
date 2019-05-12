package com.sesu8642.feudaltactics.engine;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamelogic.Kingdom;

public class HexTile {
	
	private Vector2 coordinates;
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
	
	public Vector2 getCoordinates() {
		return coordinates;
	}

	public Kingdom getKingdom() {
		return kingdom;
	}

	public void setKingdom(Kingdom kingdom) {
		this.kingdom = kingdom;
	}
	
}
