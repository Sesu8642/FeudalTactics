package com.sesu8642.feudaltactics.input;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import com.sesu8642.feudaltactics.gamelogic.gamestate.HexMapHelper;
import com.sesu8642.feudaltactics.gamelogic.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamelogic.gamestate.MapObject;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Tree;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Unit;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Unit.UnitTypes;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;

/**
 * Utility class that checks whether certain user action are allowed according
 * to the rules.
 */
public class InputValidationHelper {

	private InputValidationHelper() {
		// utility class -> prevent instantiation
		throw new AssertionError();
	}

	/**
	 * Checks whether a player is allowed to change the active kingdom.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player that attempts the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
	public static boolean checkChangeActiveKingdom(GameState gameState, Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
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

	/**
	 * Checks whether a player is allowed to pick up an object.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player that attempts the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
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
		if (!ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())) {
			return false;
		}
		if (!((Unit) tile.getContent()).isCanAct()) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether a player is allowed to place their own object.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player that attempts the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
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
		if (gameState.getActiveKingdom() != tile.getKingdom()) {
			return false;
		}
		if (tile.getContent() != null && !ClassReflection.isAssignableFrom(Tree.class, tile.getContent().getClass())) {
			// not empty or a tree
			return false;
		}
		if (tile.getContent() != null && ClassReflection.isAssignableFrom(Tree.class, tile.getContent().getClass())
				&& !ClassReflection.isAssignableFrom(Unit.class, gameState.getHeldObject().getClass())) {
			// non-unit on tree
			return false;
		}
		return true;
	}

	/**
	 * Checks whether a player is allowed to combine units.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player that attempts the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
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
		if (gameState.getActiveKingdom() != tile.getKingdom()) {
			return false;
		}
		if (!ClassReflection.isAssignableFrom(Unit.class, gameState.getHeldObject().getClass())) {
			return false;
		}
		if (!ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())) {
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

	/**
	 * Checks whether a player is allowed to conquer a tile.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player that attempts the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
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
		if (!ClassReflection.isAssignableFrom(Unit.class, gameState.getHeldObject().getClass())) {
			// not a unit
			return false;
		}
		if (tile.getContent() != null && tile.getContent().getStrength() >= gameState.getHeldObject().getStrength()) {
			// too strong object on the tile
			return false;
		}
		boolean isNextoToOwnKingdom = false;
		for (HexTile neighborTile : HexMapHelper.getNeighborTiles(gameState.getMap(), tile)) {
			if (isWater(neighborTile)) {
				// skip water
				continue;
			}
			// check if tile is next to own kingdom
			if (neighborTile.getKingdom() == gameState.getActiveKingdom()) {
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

	/**
	 * Checks whether a player is allowed to buy an object.
	 * 
	 * @param gameState game state of the current game
	 * @param cost      cost of the object
	 * @return whether the action is allowed
	 */
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

	/**
	 * Checks whether a player is allowed undo the previous action.
	 * 
	 * @return whether the action is allowed
	 */
	public static boolean checkUndoAction() {
		return (PreferencesHelper.getNoOfAutoSaves() > 1);
	}

	private static boolean isWater(HexTile tile) {
		return (tile == null);
	}

}
