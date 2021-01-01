package com.sesu8642.feudaltactics.gamestate.mapobjects;

import com.sesu8642.feudaltactics.gamestate.Kingdom;

public class Capital extends MapObject {

	static private final String spriteName = "capital";
	static private final int strength = 1;
	
	public Capital() {
	}
	
	public Capital(Kingdom kingdom) {
		super(kingdom);
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
	public MapObject getCopy(Kingdom newKingdom) {
		return new Capital(newKingdom);
	}

}
