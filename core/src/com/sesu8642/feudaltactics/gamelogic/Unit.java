package com.sesu8642.feudaltactics.gamelogic;

import com.sesu8642.feudaltactics.engine.MapObject;

public class Unit extends MapObject{

	private boolean canAct;
	private int strength;
	private final String[] spriteNames = {"sprite_peasant", "sprite_spearman", "sprite_knight", "sprite_baron"};
	private final int[] salaries = {2, 6, 18, 54};
	
	public Unit(int strength) {
		this.strength = strength;
	}
	
	public int getSalary() {
		return salaries[strength-1];
	}
	
	@Override
	public String getSpriteName() {
		return spriteNames[strength-1];
	}
}
