package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Capital extends MapObject {

	static public final String SPRITE_NAME = "capital";
	static public final int STRENGTH = 1;

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

}
