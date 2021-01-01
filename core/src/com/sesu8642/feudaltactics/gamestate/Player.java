package com.sesu8642.feudaltactics.gamestate;

import com.badlogic.gdx.graphics.Color;

public class Player {

	private Color color;
	private Type type;
	private boolean defeated = false;

	public static enum Type {
		LOCAL_PLAYER, LOCAL_BOT, REMOTE
	}

	public Player(){
	}
	
	public Player(Color color, Type type) {
		this.color = color;
		this.type = type;
	}

	public Player(Color color, boolean defeated, Type type) {
		this.color = color;
		this.defeated = defeated;
		this.type = type;
	}

	public Color getColor() {
		return color;
	}

	public boolean isDefeated() {
		return defeated;
	}

	public void setDefeated(boolean defeated) {
		this.defeated = defeated;
	}

	public Type getType() {
		return type;
	}

	public Player clone() {
		return new Player(color, defeated, type);
	}

}
