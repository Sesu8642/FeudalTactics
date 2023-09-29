// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.utils.reflect.ClassReflection;

import de.sesu8642.feudaltactics.lib.gamestate.Unit.UnitTypes;

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
	 * @param player    player attempting the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
	public static boolean checkChangeActiveKingdom(GameState gameState, Player player, HexTile tile) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
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
	 * @param player    player attempting the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
	public static boolean checkPickupObject(GameState gameState, Player player, HexTile tile) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
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
	 * @param player    player attempting the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
	public static boolean checkPlaceOwn(GameState gameState, Player player, HexTile tile) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
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
		if (tile.getContent() != null && ClassReflection.isAssignableFrom(Blocking.class, tile.getContent().getClass())
				&& !ClassReflection.isAssignableFrom(Unit.class, gameState.getHeldObject().getClass())) {
			// non-unit on blocking object
			return false;
		}
		if (tile.getContent() != null
				&& !ClassReflection.isAssignableFrom(Blocking.class, tile.getContent().getClass())) {
			// not empty or blocking object
			return false;
		}
		return true;
	}

	/**
	 * Checks whether a player is allowed to buy and place their own unit instantly,
	 * without it being in held before.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player attempting the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
	public static boolean checkBuyAndPlaceUnitInstantly(GameState gameState, Player player, HexTile tile) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
		// first check whether buying is possible
		if (!checkBuyObject(gameState, player, Unit.class)) {
			return false;
		}
		// then check whether placing is possible
		if (isWater(tile)) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (gameState.getActiveKingdom() != tile.getKingdom()) {
			return false;
		}
		if (tile.getContent() != null
				&& !ClassReflection.isAssignableFrom(Blocking.class, tile.getContent().getClass())) {
			// not empty or blocking object
			return false;
		}
		return true;
	}

	/**
	 * Checks whether a player is allowed to buy and place a castle instantly,
	 * without it being in held before.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player attempting the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
	public static boolean checkBuyAndPlaceCastleInstantly(GameState gameState, Player player, HexTile tile) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
		// first check whether buying is possible
		Kingdom activeKingdom = gameState.getActiveKingdom();
		if (activeKingdom == null) {
			return false;
		}
		if (activeKingdom.getSavings() < Castle.COST) {
			return false;
		}
		if (gameState.getHeldObject() != null) {
			return false;
		}
		// then check whether placing is possible
		if (isWater(tile)) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (gameState.getActiveKingdom() != tile.getKingdom()) {
			return false;
		}
		if (tile.getContent() != null) {
			// not empty
			return false;
		}
		return true;
	}

	/**
	 * Checks whether a player is allowed to combine units.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player attempting the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
	public static boolean checkCombineUnits(GameState gameState, Player player, HexTile tile) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
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
	 * @param player    player attempting the action
	 * @param tile      tile that was clicked
	 * @return whether the action is allowed
	 */
	public static boolean checkConquer(GameState gameState, Player player, HexTile tile) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
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
			if (tile.getKingdom() != null && neighborTile.getKingdom() == tile.getKingdom() && neighborContent != null
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

	/**
	 * Checks whether the player is allowed to end the current turn.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player attempting the action
	 * @return whether the action is allowed
	 */
	public static boolean checkEndTurn(GameState gameState, Player player) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
		return (gameState.getHeldObject() == null);
	}

	/**
	 * Checks whether a player is allowed to buy an object.
	 * 
	 * @param gameState game state of the current game
	 * @param player    player attempting the action
	 * @param cost      cost of the object
	 * @return whether the action is allowed
	 */
	public static boolean checkBuyObject(GameState gameState, Player player, Class<?> targetClass) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
		Kingdom activeKingdom = gameState.getActiveKingdom();
		if (activeKingdom == null) {
			return false;
		}
		int cost;
		if (Unit.class.isAssignableFrom(targetClass)) {
			cost = Unit.COST;
		} else if (Castle.class.isAssignableFrom(targetClass)) {
			cost = Castle.COST;
		} else {
			throw new IllegalStateException("Unexpected class to buy " + targetClass);
		}
		if (activeKingdom.getSavings() < cost) {
			return false;
		}
		// allow upgrading a held unit
		if (!(gameState.getHeldObject() == null || (Unit.class.isAssignableFrom(gameState.getHeldObject().getClass())
				&& ((Unit) gameState.getHeldObject()).getStrength() < UnitTypes.strongest().strength()
				&& Unit.class.isAssignableFrom(targetClass)))) {
			return false;
		}
		return true;
	}

	/**
	 * Checks whether a player is allowed undo the previous action.
	 * 
	 * @param gameState     game state of the current game
	 * @param player        player attempting the action
	 * @param noOfAutoSaves number of available autosaves
	 * @return whether the action is allowed
	 */
	public static boolean checkUndoAction(GameState gameState, Player player, int noOfAutoSaves) {
		if (!isCorrectPlayersTurn(gameState, player)) {
			return false;
		}
		return (noOfAutoSaves > 1);
	}

	private static boolean isWater(HexTile tile) {
		return (tile == null);
	}

	private static boolean isCorrectPlayersTurn(GameState gameState, Player actingPlayer) {
		return gameState.getActivePlayer() == actingPlayer;
	}

}
