package com.sesu8642.feudaltactics.engine;

abstract public class MapObject {

	private final String spriteName = "";
	private final int strength = 0;
	private HexTile tile;
	
	public String getSpriteName() {
		return spriteName;
	}
	
	public int getStrength() {
		return strength;
	}
	
	public HexTile getTile() {
		return tile;
	}
}
