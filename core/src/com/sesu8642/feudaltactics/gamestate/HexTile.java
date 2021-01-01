package com.sesu8642.feudaltactics.gamestate;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;

public class HexTile {

	private Player player;
	private MapObject content;
	private Kingdom kingdom;
	private Vector2 position;
	private ArrayList<HexTile> cachedNeighborTiles;

	public HexTile() {
	}

	public HexTile(Player player, Vector2 position) {
		this.player = player;
		this.position = position;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
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
		if (kingdom != null) {
			this.player = kingdom.getPlayer();
		}
	}

	public Vector2 getPosition() {
		return position;
	}

	public ArrayList<HexTile> getCachedNeighborTiles() {
		return cachedNeighborTiles;
	}

	public void setCachedNeighborTiles(ArrayList<HexTile> neighborTiles) {
		this.cachedNeighborTiles = neighborTiles;
	}

	@Override
	public String toString() {
		String kingdomStr = kingdom == null ? "null" : kingdom.toString();
		String contentStr = content == null ? "null" : content.toString();
		return "Position: " + position.toString() + " Color: " + player.getColor().toString() + ", Kingdom: "
				+ kingdomStr + ", Content: " + contentStr;
	}
}
