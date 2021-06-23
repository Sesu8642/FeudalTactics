package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Tree extends MapObject {

	static public final String SPRITE_NAME = "tree";
	static private final int STRENGTH = 0;

	@Override
	public String getSpriteName() {
		return SPRITE_NAME;
	}

	@Override
	public int getStrength() {
		return STRENGTH;
	}

	@Override
	public Tree getCopy() {
		return new Tree();
	}

}
