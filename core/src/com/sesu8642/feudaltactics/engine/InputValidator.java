package com.sesu8642.feudaltactics.engine;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Castle;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Tree;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;

public class InputValidator {

	// TODO: when validating multiplayer inputs, make sure that it's the player's
	// turn before calling check*

	public enum TapAction {
		NONE, PICK_UP, PLACE_OWN, COMBINE_UNITS, CONQUER
	}

	private GameController gameController;

	public InputValidator(GameController gameController) {
		this.gameController = gameController;
	}

	public void inputTap(Vector2 worldCoords) {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		HexMap map = gameController.getGameState().getMap();
		Vector2 hexCoords = map.worldCoordsToHexCoords(worldCoords);
		Player player = gameController.getGameState().getActivePlayer();
		HexTile tile = map.getTiles().get(hexCoords);
		// print info
		gameController.printTileInfo(hexCoords);
		if (checkChangeActiveKingdom(player, tile)) {
			// activate kingdom
			gameController.activateKingdom(tile.getKingdom());
		}
		TapAction action = determineTapAction(player, tile);
		switch (action) {
		case PICK_UP:
			if (checkPickupObject(player, tile)) {
				gameController.pickupObject(tile);
			}
			break;
		case PLACE_OWN:
			if (checkPlaceOwn(player, tile)) {
				gameController.placeOwn(tile);
			}
			break;
		case COMBINE_UNITS:
			if (checkCombineUnits(player, tile)) {
				gameController.combineUnits(tile);
			}
		case CONQUER:
			if (checkConquer(player, tile)) {
				gameController.conquer(tile);
			}
			break;
		case NONE:
			break;
		}
	}

	public TapAction determineTapAction(Player player, HexTile tile) {
		// determine action
		if (tile == null) {
			return TapAction.NONE;
		}
		if (gameController.getGameState().getHeldObject() == null) {
			// pick up object
			return TapAction.PICK_UP;
		} else {
			// place object
			if (tile.getPlayer() != null && tile.getPlayer() == player) {
				if (tile.getContent() == null || tile.getContent().getClass().isAssignableFrom(Tree.class)) {
					return TapAction.PLACE_OWN;
				} else {
					return TapAction.COMBINE_UNITS;
				}
			} else {
				return TapAction.CONQUER;
			}
		}
	}

	public void inputEndTurn() {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		if (checkEndTurn()) {
			gameController.endTurn();
		}
	}

	public void inputBuyPeasant() {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		if (checkBuyObject(Unit.COST)) {
			gameController.buyPeasant();
		}
	}

	public void inputBuyCastle() {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		if (checkBuyObject(Castle.COST)) {
			gameController.buyCastle();
		}
	}

	public void inputUndo() {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		if (checkUndoAction()) {
			gameController.undoLastAction();
		}
	}

	private boolean isWater(HexTile tile) {
		return (tile == null);
	}

	private boolean isActivePlayerLocalHuman() {
		return (gameController.getGameState().getActivePlayer().getType() == Player.Type.LOCAL_PLAYER);
	}

	public boolean checkChangeActiveKingdom(Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() != null) {
			return false;
		}
		if (tile.getKingdom() == null) {
			return false;
		}
		if (gameController.getGameState().getActiveKingdom() == tile.getKingdom()) {
			return false;
		}
		return true;
	}

	public boolean checkPickupObject(Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() != null) {
			return false;
		}
		if (tile.getContent() == null) {
			return false;
		}
		if (tile.getPlayer() != player) {
			return false;
		}
		if (!tile.getContent().getClass().isAssignableFrom(Unit.class)) {
			return false;
		}
		if (!((Unit) tile.getContent()).isCanAct()) {
			return false;
		}

		return true;
	}

	public boolean checkPlaceOwn(Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() == null) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (tile.getKingdom() == null) {
			return false;
		}
		if (gameController.getGameState().getHeldObject().getKingdom() != tile.getKingdom()) {
			return false;
		}
		if (tile.getContent() != null && !tile.getContent().getClass().isAssignableFrom(Tree.class)) {
			// not empty or a tree
			return false;
		}
		if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Tree.class)
				&& !gameController.getGameState().getHeldObject().getClass().isAssignableFrom(Unit.class)) {
			// non-unit on tree
			return false;
		}
		return true;
	}

	public boolean checkCombineUnits(Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() == null) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (tile.getKingdom() == null) {
			return false;
		}
		if (gameController.getGameState().getHeldObject().getKingdom() != tile.getKingdom()) {
			return false;
		}
		if (!gameController.getGameState().getHeldObject().getClass().isAssignableFrom(Unit.class)) {
			return false;
		}
		if (!tile.getContent().getClass().isAssignableFrom(Unit.class)) {
			return false;
		}
		if (((Unit) gameController.getGameState().getHeldObject()).getUnitType() != UnitTypes.PEASANT
				&& ((Unit) tile.getContent()).getUnitType() != UnitTypes.PEASANT) {
			// not at least one peasant
			return false;
		}

		return true;
	}

	public boolean checkConquer(Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() == null) {
			return false;
		}
		if (tile.getPlayer() == player) {
			return false;
		}
		if (!gameController.getGameState().getHeldObject().getClass().isAssignableFrom(Unit.class)) {
			// not a unit
			return false;
		}
		if (tile.getContent() != null
				&& tile.getContent().getStrength() >= gameController.getGameState().getHeldObject().getStrength()) {
			// too strong object on the tile
			return false;
		}
		boolean isNextoToOwnKingdom = false;
		for (HexTile neighborTile : gameController.getGameState().getMap().getNeighborTiles(tile.getPosition())) {
			if (isWater(neighborTile)) {
				// skip water
				continue;
			}
			// check if tile is next to own kingdom
			if (neighborTile.getKingdom() == gameController.getGameState().getHeldObject().getKingdom()) {
				isNextoToOwnKingdom = true;
			}
			MapObject neighborContent = neighborTile.getContent();
			// check if there is no stronger object next to it protecting it
			if (neighborTile.getKingdom() == tile.getKingdom() && neighborContent != null
					&& neighborContent.getStrength() >= gameController.getGameState().getHeldObject().getStrength()) {
				return false;
			}
		}
		if (!isNextoToOwnKingdom) {
			// not next to the unit's kingdom
			return false;
		}
		return true;
	}

	public boolean checkEndTurn() {
		return (gameController.getGameState().getHeldObject() == null);
	}

	public boolean checkBuyObject(int cost) {
		Kingdom activeKingdom = gameController.getGameState().getActiveKingdom();
		if (activeKingdom == null) {
			return false;
		}
		if (activeKingdom.getSavings() < cost) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() != null) {
			return false;
		}
		return true;
	}

	public boolean checkUndoAction() {
		return (gameController.getUndoStates().size() > 0);
	}

}
