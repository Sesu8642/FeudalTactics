package com.sesu8642.feudaltactics.gamestate;

import com.badlogic.gdx.graphics.Color;

public class Player {

	private Color color;

	public static enum Type {
		LOCAL_PLAYER, LOCAL_BOT, REMOTE
	}

	private Type type;

	public Player(Color color, Type type) {
		this.color = color;
		this.type = type;
	}

	public Color getColor() {
		return color;
	}

	public Type getType() {
		return type;
	}
	
	public Player clone() {
		return new Player(color, type);
	}

}
