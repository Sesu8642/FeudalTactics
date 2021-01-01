package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Capital extends MapObject {

	static private final String spriteName = "capital";
	static private final int strength = 1;
	
	public Capital() {
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
		return new Capital();
	}

}
