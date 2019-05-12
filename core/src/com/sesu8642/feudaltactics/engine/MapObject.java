package com.sesu8642.feudaltactics.engine;

abstract public class MapObject {

	private final int strength = 0;
	private HexTile tile;
	
	public abstract String getSpriteName();
	
	public int getStrength() {
		return strength;
	}
	
	public HexTile getTile() {
		return tile;
	}
	
}
