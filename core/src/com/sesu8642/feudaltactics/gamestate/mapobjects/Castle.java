package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Castle implements MapObject {

	public static final int COST = 15;
	public static final String SPRITE_NAME = "castle";
	public static final int STRENGTH = 2;

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
