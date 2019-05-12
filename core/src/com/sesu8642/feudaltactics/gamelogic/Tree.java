package com.sesu8642.feudaltactics.gamelogic;

import com.sesu8642.feudaltactics.engine.MapObject;

public class Tree extends MapObject{

	private final String spriteName = "sprite_tree";
	private final int strength = 0;
	
	@Override
	public String getSpriteName() {
		return spriteName;
	}
	
}
