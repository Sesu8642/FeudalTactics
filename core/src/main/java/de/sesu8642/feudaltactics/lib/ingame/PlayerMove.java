// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame;

import com.badlogic.gdx.math.Vector2;

/**
 * Information about an action a player attempts.
 */
public class PlayerMove {

	/**
	 * Types of possible player moves.
	 */
	public enum PlayerMoveType {
		PICK_UP, PLACE_OWN, COMBINE_UNITS, CONQUER, BUY_PEASANT, BUY_CASTLE, ACTIVATE_KINGDOM
	}

	private PlayerMoveType playerMoveType;

	private Vector2 tilePosition;

	private PlayerMove() {
		// for JSON serialization only
	}

	private PlayerMove(PlayerMoveType playerMoveType, Vector2 tilePosition) {
		this.playerMoveType = playerMoveType;
		this.tilePosition = tilePosition;
	}

	static PlayerMove pickUp(Vector2 tilePosition) {
		return new PlayerMove(PlayerMoveType.PICK_UP, tilePosition);
	}

	static PlayerMove placeOwn(Vector2 tilePosition) {
		return new PlayerMove(PlayerMoveType.PLACE_OWN, tilePosition);
	}

	static PlayerMove combineUnits(Vector2 tilePosition) {
		return new PlayerMove(PlayerMoveType.COMBINE_UNITS, tilePosition);
	}

	static PlayerMove conquer(Vector2 tilePosition) {
		return new PlayerMove(PlayerMoveType.CONQUER, tilePosition);
	}

	static PlayerMove buyPeasant() {
		return new PlayerMove(PlayerMoveType.BUY_PEASANT, null);
	}

	static PlayerMove buyCastle() {
		return new PlayerMove(PlayerMoveType.BUY_CASTLE, null);
	}

	static PlayerMove activateKingdom(Vector2 tilePosition) {
		return new PlayerMove(PlayerMoveType.ACTIVATE_KINGDOM, tilePosition);
	}

	public PlayerMoveType getPlayerActionType() {
		return playerMoveType;
	}

	public Vector2 getTilePosition() {
		return tilePosition;
	}

}
