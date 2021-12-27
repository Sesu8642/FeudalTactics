package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Tree implements MapObject {

	public static final String SPRITE_NAME = "tree";
	private static final int STRENGTH = 0;

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

	@Override
	public String toString() {
		return getClass().getName();
	}

}
