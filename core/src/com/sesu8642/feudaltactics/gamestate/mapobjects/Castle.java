package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Castle extends MapObject {

	static public final int COST = 15;
	static private final String spriteName = "castle";
	static private final int strength = 2;
	
	public Castle() {
		super();
	}
	
	@Override
	public String getSpriteName() {
		return spriteName;
	}

	@Override
	public int getStrength() {
		return strength;
	}

	@Override
	public MapObject getCopy() {
		return new Castle();
	}

}
