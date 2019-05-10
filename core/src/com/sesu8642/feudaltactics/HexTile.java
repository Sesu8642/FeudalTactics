package com.sesu8642.feudaltactics;

import com.badlogic.gdx.math.Vector2;

public class HexTile {
	
	private Vector2 coordinates;

	public Vector2 getCoordinates() {
		return coordinates;
	}

	private Player player;
	private MapObject content;
	
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
}
