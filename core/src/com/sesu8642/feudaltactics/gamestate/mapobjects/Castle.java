package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Castle implements MapObject {

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
	public Castle getCopy() {
		return new Castle();
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

}
