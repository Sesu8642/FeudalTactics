package com.sesu8642.feudaltactics.gamestate.mapobjects;

public class Tree extends MapObject {

	static private final String spriteName = "tree";
	private final int strength = 0;

	public Tree() {
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
		return new Tree();
	}

}
