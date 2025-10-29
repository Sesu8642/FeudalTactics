// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame;

import com.badlogic.gdx.math.Vector2;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Information about an action a player attempts.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerMove {

    private PlayerMoveType playerMoveType;
    @Getter
    private Vector2 tilePosition;

    private PlayerMove(PlayerMoveType playerMoveType, Vector2 tilePosition) {
        this.playerMoveType = playerMoveType;
        this.tilePosition = tilePosition;
    }

    public static PlayerMove pickUp(Vector2 tilePosition) {
        return new PlayerMove(PlayerMoveType.PICK_UP, tilePosition);
    }

    public static PlayerMove placeOwn(Vector2 tilePosition) {
        return new PlayerMove(PlayerMoveType.PLACE_OWN, tilePosition);
    }

    public static PlayerMove combineUnits(Vector2 tilePosition) {
        return new PlayerMove(PlayerMoveType.COMBINE_UNITS, tilePosition);
    }

    public static PlayerMove conquer(Vector2 tilePosition) {
        return new PlayerMove(PlayerMoveType.CONQUER, tilePosition);
    }

    public static PlayerMove buyPeasant() {
        return new PlayerMove(PlayerMoveType.BUY_PEASANT, null);
    }

    public static PlayerMove buyCastle() {
        return new PlayerMove(PlayerMoveType.BUY_CASTLE, null);
    }

    public static PlayerMove buyAndPlacePeasant(Vector2 tilePosition) {
        return new PlayerMove(PlayerMoveType.BUY_AND_PLACE_PEASANT, tilePosition);
    }

    public static PlayerMove buyAndPlaceCastle(Vector2 tilePosition) {
        return new PlayerMove(PlayerMoveType.BUY_AND_PLACE_CASTLE, tilePosition);
    }

    public static PlayerMove activateKingdom(Vector2 tilePosition) {
        return new PlayerMove(PlayerMoveType.ACTIVATE_KINGDOM, tilePosition);
    }

    public static PlayerMove undoLastMove() {
        return new PlayerMove(PlayerMoveType.UNDO_LAST_MOVE, null);
    }

    public static PlayerMove endTurn() {
        return new PlayerMove(PlayerMoveType.END_TURN, null);
    }

    public PlayerMoveType getPlayerActionType() {
        return playerMoveType;
    }

    @Override
    public String toString() {
        return playerMoveType + ", tilePosition=" + tilePosition;
    }

    /**
     * Types of possible player moves.
     */
    public enum PlayerMoveType {
        PICK_UP, PLACE_OWN, COMBINE_UNITS, CONQUER, BUY_PEASANT, BUY_CASTLE, BUY_AND_PLACE_PEASANT,
        BUY_AND_PLACE_CASTLE, ACTIVATE_KINGDOM, UNDO_LAST_MOVE, END_TURN
    }

}
