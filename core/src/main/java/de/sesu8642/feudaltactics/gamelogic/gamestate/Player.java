// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.gamelogic.gamestate;

import java.util.Objects;

import com.badlogic.gdx.graphics.Color;

/** A human or bot player participating in a game. **/
public class Player {

	private Color color;
	private Type type;
	private boolean defeated = false;

	/** Type of a player. **/
	public enum Type {
		LOCAL_PLAYER, LOCAL_BOT, REMOTE
	}

	public Player() {
	}

	public Player(Color color, Type type) {
		this.color = color;
		this.type = type;
	}

	/**
	 * Constructor.
	 * 
	 * @param color    color this player's tiles have
	 * @param defeated whether this player is defeated
	 * @param type     player type
	 */
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

	/**
	 * Returns a deep copy of the original. Exception: color field is the same
	 * instance as the original one
	 * 
	 * @return copy
	 */
	public static Player copyOf(Player original) {
		return new Player(original.getColor(), original.isDefeated(), original.getType());
	}

	@Override
	public int hashCode() {
		return Objects.hash(color, defeated, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Player other = (Player) obj;
		return Objects.equals(color, other.color) && defeated == other.defeated && type == other.type;
	}

	@Override
	public String toString() {
		return String.format("Player [color=%s, type=%s, defeated=%s]", color, type, defeated);
	}

}
