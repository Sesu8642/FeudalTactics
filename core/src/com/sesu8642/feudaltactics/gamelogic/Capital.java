package com.sesu8642.feudaltactics.gamelogic;

import com.sesu8642.feudaltactics.engine.MapObject;

public class Capital extends MapObject{
	
	private final String spriteName = "sprite_capital";
	private final int strength = 1;
	
	private int money;

	public int getMoney() {
		return money;
	}

	@Override
	public String getSpriteName() {
		return spriteName;
	}
	
}
