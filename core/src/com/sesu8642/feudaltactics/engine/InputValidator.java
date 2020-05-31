package com.sesu8642.feudaltactics.engine;

import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Tree;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;

import com.badlogic.gdx.utils.reflect.ClassReflection;

public class InputValidator {

	public static boolean checkChangeActiveKingdom(GameState gameState, Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		System.out.println(gameState.getHeldObject());
		if (gameState.getHeldObject() != null) {
			return false;
		}
		if (tile.getKingdom() == null) {
			return false;
		}
		if (gameState.getActiveKingdom() == tile.getKingdom()) {
			return false;
		}
		return true;
	}

	public static boolean checkPickupObject(GameState gameState, Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (gameState.getHeldObject() != null) {
			return false;
		}
		if (tile.getContent() == null) {
			return false;
		}
		if (tile.getPlayer() != player) {
			return false;
		}
		if (!ClassReflection.isAssignableFrom(tile.getContent().getClass(), Unit.class)) {
			return false;
		}
		if (!((Unit) tile.getContent()).isCanAct()) {
			return false;
		}

		return true;
	}

	public static boolean checkPlaceOwn(GameState gameState, Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (gameState.getHeldObject() == null) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (tile.getKingdom() == null) {
			return false;
		}
		if (gameState.getHeldObject().getKingdom() != tile.getKingdom()) {
			return false;
		}
		if (tile.getContent() != null && !ClassReflection.isAssignableFrom(tile.getContent().getClass(), Tree.class)) {
			// not empty or a tree
			return false;
		}
		if (tile.getContent() != null && ClassReflection.isAssignableFrom(tile.getContent().getClass(), Tree.class)
				&& !ClassReflection.isAssignableFrom(gameState.getHeldObject().getClass(), Unit.class)) {
			// non-unit on tree
			return false;
		}
		return true;
	}

	public static boolean checkCombineUnits(GameState gameState, Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (tile.getContent() == null) {
			return false;
		}
		if (gameState.getHeldObject() == null) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (tile.getKingdom() == null) {
			return false;
		}
		if (gameState.getHeldObject().getKingdom() != tile.getKingdom()) {
			return false;
		}
		if (!ClassReflection.isAssignableFrom(gameState.getHeldObject().getClass(), Unit.class)) {
			return false;
		}
		if (!ClassReflection.isAssignableFrom(tile.getContent().getClass(), Unit.class)) {
			return false;
		}
		if (((Unit) gameState.getHeldObject()).getUnitType() != UnitTypes.PEASANT
				&& ((Unit) tile.getContent()).getUnitType() != UnitTypes.PEASANT) {
			// not at least one peasant
			return false;
		}
		if (((Unit) gameState.getHeldObject()).getUnitType() == UnitTypes.BARON
				|| ((Unit) tile.getContent()).getUnitType() == UnitTypes.BARON) {
			// cannot upgrade a baron
			return false;
		}
		return true;
	}

	public static boolean checkConquer(GameState gameState, Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (gameState.getHeldObject() == null) {
			return false;
		}
		if (tile.getPlayer() == player) {
			return false;
		}
		if (!ClassReflection.isAssignableFrom(gameState.getHeldObject().getClass(), Unit.class)) {
			// not a unit
			return false;
		}
		if (tile.getContent() != null && tile.getContent().getStrength() >= gameState.getHeldObject().getStrength()) {
			// too strong object on the tile
			return false;
		}
		boolean isNextoToOwnKingdom = false;
		for (HexTile neighborTile : gameState.getMap().getNeighborTiles(tile)) {
			if (isWater(neighborTile)) {
				// skip water
				continue;
			}
			// check if tile is next to own kingdom
			if (neighborTile.getKingdom() == gameState.getHeldObject().getKingdom()) {
				isNextoToOwnKingdom = true;
			}
			MapObject neighborContent = neighborTile.getContent();
			// check if there is no stronger object next to it protecting it
			if (neighborTile.getKingdom() == tile.getKingdom() && neighborContent != null
					&& neighborContent.getStrength() >= gameState.getHeldObject().getStrength()) {
				return false;
			}
		}
		if (!isNextoToOwnKingdom) {
			// not next to the unit's kingdom
			return false;
		}
		return true;
	}

	public static boolean checkEndTurn(GameState gameState) {
		return (gameState.getHeldObject() == null);
	}

	public static boolean checkBuyObject(GameState gameState, int cost) {
		Kingdom activeKingdom = gameState.getActiveKingdom();
		if (activeKingdom == null) {
			return false;
		}
		if (activeKingdom.getSavings() < cost) {
			return false;
		}
		if (gameState.getHeldObject() != null) {
			return false;
		}
		return true;
	}

	public static boolean checkUndoAction(GameController gameController) {
		return (gameController.getUndoStates().size() > 0);
	}

	private static boolean isWater(HexTile tile) {
		return (tile == null);
	}

}
