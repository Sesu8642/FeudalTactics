package com.sesu8642.feudaltactics.gamestate.mapobjects;

import com.sesu8642.feudaltactics.gamestate.Kingdom;

public class Tree extends MapObject {

	static private final String spriteName = "tree";
	private final int strength = 0;

	public Tree() {
	}

	public Tree(Kingdom kingdom) {
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
		return new Tree(newKingdom);
	}

}
