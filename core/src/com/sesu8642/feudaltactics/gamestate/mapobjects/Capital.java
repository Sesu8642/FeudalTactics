package com.sesu8642.feudaltactics.gamestate.mapobjects;

/**
 * Map object representing the capital of a kingdom. If the capital is
 * destroyed, the kingdom's money is lost.
 **/
public class Capital implements MapObject {

	public static final String SPRITE_NAME = "capital";
	public static final int STRENGTH = 1;

	@Override
	public String getSpriteName() {
		return SPRITE_NAME;
	}

	@Override
	public int getStrength() {
		return STRENGTH;
	}

	@Override
	public Capital getCopy() {
		return new Capital();
	}

	@Override
	public String toString() {
		return getClass().getName();
	}

}
