package com.sesu8642.feudaltactics.gamestate.mapobjects;

import com.sesu8642.feudaltactics.gamestate.Kingdom;

abstract public class MapObject {

	private Kingdom kingdom;

	public MapObject(Kingdom kingdom){
		this.kingdom = kingdom;
	}
	
	public Kingdom getKingdom() {
		return kingdom;
	}

	public void setKingdom(Kingdom kingdom) {
		this.kingdom = kingdom;
	}

	public abstract String getSpriteName();

	public abstract int getStrength();

	public abstract MapObject getCopy(Kingdom kingdom);

}
