package com.sesu8642.feudaltactics.gamestate.mapobjects;

import com.sesu8642.feudaltactics.gamestate.Kingdom;

public class Tree extends MapObject{

	private final String spriteName = "sprite_tree";
	private final int strength = 0;
	
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
	
}
