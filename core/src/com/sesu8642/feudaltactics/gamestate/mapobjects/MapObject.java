package com.sesu8642.feudaltactics.gamestate.mapobjects;

abstract public class MapObject {

	public MapObject() {
	}

	public abstract String getSpriteName();

	public abstract int getStrength();

	public abstract MapObject getCopy();

	@Override
	public String toString() {
		return getClass().getName();
	}
}
