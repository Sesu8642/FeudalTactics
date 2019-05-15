package com.sesu8642.feudaltactics.gamestate.mapobjects;

import com.sesu8642.feudaltactics.gamestate.MapObject;

public class Capital extends MapObject {

	private final String spriteName = "sprite_capital";
	private final int strength = 1;

	@Override
	public String getSpriteName() {
		return spriteName;
	}

}
