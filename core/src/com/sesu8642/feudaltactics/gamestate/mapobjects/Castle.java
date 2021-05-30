package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Castle extends MapObject {

	static public final int COST = 15;
	static public final String SPRITE_NAME = "castle";
	static public final int STRENGTH = 2;

	@Override
	public String getSpriteName() {
		return SPRITE_NAME;
	}

	@Override
	public int getStrength() {
		return STRENGTH;
	}

	@Override
	public MapObject getCopy() {
		return new Castle();
	}

}
